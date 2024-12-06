import groovy.json.JsonSlurper

// GitHub repository URL and folder path
def gitRepo = "https://github.com/PreethikhaTessell/jenkins-code.git"
def jobsFolder = "jobs"

// Clone repository and fetch JSON files
pipelineJob('FetchGitHubJobs') {
    description('Job to fetch JSON files from GitHub and create/update Jenkins jobs.')
    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Clone GitHub Repo') {
                            steps {
                                git branch: 'main', url: '${gitRepo}'
                            }
                        }
                        stage('Process JSON Files') {
                            steps {
                                script {
                                    def jsonFolder = new File("${jobsFolder}")
                                    jsonFolder.eachFileMatch(~/.*\\.json/) { file ->
                                        def json = new JsonSlurper().parse(file)
                                        def config = json.configuration

                                        // Dynamically create or update jobs
                                        if (config.type == 'freestyle') {
                                            job(config.name) {
                                                description(config.description)

                                                if (config.parameters) {
                                                    parameters {
                                                        config.parameters.each { param ->
                                                            if (param.type == 'StringParameterDefinition') {
                                                                stringParam(param.name, param.defaultValue, param.description)
                                                            }
                                                        }
                                                    }
                                                }

                                                steps {
                                                    config.steps.each { step ->
                                                        shell(step.shellCommand)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            """)
            sandbox()
        }
    }
}
