def call(body) {
    //docker setup
    currentBuild.displayName = jobDetails.prettyName()
    String dockerRegistry = '320083444745.dkr.ecr.us-west-2.amazonaws.com'
    String dockerRepo = "${dockerRegistry}/10e/builders"
    String dockerImage = "${dockerRepo}:${config.name}_${jobDetails.branchName().replaceAll(/[^a-zA-Z0-9-]/, '_')}"

    pipeline {
        agent any
        
        stages {

            stage('init') {
                environment {
                    ARTIFACTORY = credentials('/jenkins/artifactory')
                }
                steps {
                    script {
                        sshagent(['/jenkins/github.key']) {
                            sh '''
                                git submodule init
                                git submodule update --recursive --remote
                                '''
                        }
                    }
                }
                parallel {
                    stage('iOS') {
                        agent {
                            node {
                                label 'ios'
                            }

                        }
                        steps {
                            echo 'ios Test And Build here'
                        }
                    }
                    stage('android') {
                        agent {
                            node {
                                label 'shared'
                            }

                        }
                        steps {
                            echo 'android init'
                            script {
                                docker.build("runmymind/docker-android-sdk")
                            }

                        }
                    }
                    stage('web') {
                        agent {
                            node {
                                label 'shared'
                            }

                        }
                        steps {
                            echo 'web init'
                        }
                    }
                }
            }
            stage('Test And build') {
                parallel {
                    stage('iOS') {
                        agent {
                            node {
                                label 'ios'
                            }

                        }
                        steps {
                            echo 'ios Test And Build'
                        }
                    }
                    stage('android') {
                        agent {
                            node {
                                label 'shared'
                            }

                        }
                        steps {
                            
                        }
                    }
                    stage('web') {
                        agent {
                            node {
                                label 'shared'
                            }

                        }
                        steps {
                            echo 'web build'
                        }
                    }
                }
            }
            stage('Deploy') {
                parallel {
                    stage('Deploy') {
                        agent any
                        steps {
                            echo 'deploy'
                        }
                    }
                    stage('iOS') {
                        agent {
                            node {
                                label 'ios'
                            }

                        }
                        steps {
                            echo 'iOS Deploy'
                        }
                    }
                    stage('android') {
                        agent {
                            node {
                                label 'shared'
                            }

                        }
                        steps {
                            echo 'android deploy'
                        }
                    }
                    stage('web') {
                        agent {
                            node {
                                label 'shared'
                            }

                        }
                        steps {
                            echo 'web deploy'
                        }
                    }
                }
            }
        }
    }
}