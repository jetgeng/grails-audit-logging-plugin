/* Copyright 2006-2015 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import grails.util.GrailsNameUtils

includeTargets << new File(grailsAuditLoggingPluginDir, 'scripts/_ALCommon.groovy')

USAGE = '''
Usage: grails al-quickstart <domain-class-package> <auditLog-class-name>

Creates a auditLogEvent class in the specified package.

Example: grails al-quickstart com.yourapp AuditLogEvent
'''

includeTargets << grailsScript('_GrailsBootstrap')

packageName = ''
auditLogClassName = ''

target(alQuickstart: 'Creates artifacts for the Audit Logging plugin') {
	depends(checkVersion, configureProxy, packageApp, classpath)

	if (!configure()) {
		return 1
	}

	updateConfig()

	printMessage '''
*******************************************************
* Created auditLogging-related domain class. Your     *
* grails-app/conf/Config.groovy has been updated with *
* the class names of the configured domain classes;   *
* please verify that the values are correct.          *
*******************************************************
'''
}

private boolean configure() {

	def argValues = parseArgs()
	if (!argValues) {
		return false
	}

	if (argValues.size() == 2) {
		(packageName, auditLogClassName) = argValues
	}

	templateAttributes = [packageName: packageName,
												auditLogClassName: auditLogClassName]

	true
}

private void createDomains() {

	String dir = packageToDir(packageName)
	String domainDir = "$appDir/domain/$dir"
	generateFile "$templateDir/AuditLogEvent.groovy.template", "$domainDir${auditLogClassName}.groovy"
}

private void updateConfig() {

	def configFile = new File(appDir, 'conf/Config.groovy')
	if (!configFile.exists()) {
		printMessage "conf/Config.groovy not found. Please update the config file yourself."
		return
	}

  if (configFile =~ /^((?!\/\/).)*auditLog.auditLogDomainClassName$/){
    printMessage "auditLog.auditLogDomainClassName already set in Config.groovy. Please update it accordinghly yourself. "
  } else {
    configFile.withWriterAppend { BufferedWriter writer ->
      writer.newLine()
      writer.newLine()
      writer.writeLine '// Added by the Audit Logging plugin:'
      writer.writeLine "auditLog.auditLogDomainClassName = '${packageName}.$userClassName'"
      writer.newLine()
    }
  }
}

private parseArgs() {

	def args = argsMap.params

	if (2 == args.size()) {
		printMessage "Creating auditLogEvent class ${args[1]} in package ${args[0]}"
		return args
	}

	errorMessage USAGE
	null
}

setDefaultTarget 'asQuickstart'
