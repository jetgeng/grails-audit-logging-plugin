package grails.plugins.orm.auditable.utils

import grails.core.GrailsApplication
import grails.plugins.orm.auditable.AuditLogListenerUtil
import grails.util.Holders
import grails.web.http.HttpHeaders
import grails.web.mapping.UrlMapping
import grails.web.mapping.UrlMappingInfo
import grails.web.mapping.UrlMappingsHolder
import org.grails.web.mime.HttpServletResponseExtension
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.expression.Expression
import org.springframework.expression.ParseException
import org.springframework.http.HttpMethod

/**
 * Helper methods in Groovy.
 *
 * Taken from { @see
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class ReflectionUtils {

  private static final Logger log = LoggerFactory.getLogger(this)

  // set at startup
  static GrailsApplication application

  private ReflectionUtils() {
    // static only
  }

  static getConfigProperty(String name) {
    def value = AuditLogListenerUtil.auditLogConfig
    for (String part in name.split('\\.')) {
      value = value."$part"
    }
    value
  }

  static void setConfigProperty(String name, value) {
    def config = AuditLogListenerUtil.auditLogConfig
    def parts = name.split('\\.') as List
    name = parts.remove(parts.size() - 1)

    for (String part in parts) {
      config = config."$part"
    }

    config."$name" = value
  }

  static ConfigObject getAuditLogConfig() {
    def grailsConfig = getApplication().config
    grailsConfig.auditLog
  }

  static void setAuditLogConfig(ConfigObject c) { getApplication().config.grails.plugin.springsecurity = c }

  private static lookupPropertyValue(o, String name) {
    o."${getConfigProperty(name)}"
  }

  private static GrailsApplication getApplication() {
    if (!application) {
      application = Holders.grailsApplication
    }
    application
  }

}
