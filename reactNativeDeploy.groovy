import co.rival.jenkins.pipeline.trusted.Metrics

/**
 * Deploy a Rival React Native App
 *
 * iOS and Android artifacts will be deployed to HockeyApp.
 * RN web will be deployed to ???
 *
 * Assumes your app repository has the following:
 *  - A Dockerfile that defines a container within which your app resides
 *      the Dockerfile will be passed an NPM_USER and NPM_PASS build-arg which
 *      should be used to set up npm for installing node modules
 *  - Your app defines `yarn checkall`, which should run your entire desired
 *    test suite, including jest/enzyme tests
 *  - Your app contains a `jenkins_scripts/deploy.sh` script, which handles the
 *    deployment step (React app only)
 *
 * If your app contains our `styleguide` as a submodule, this utility will ensure
 * it has been checked out before running the `checkall` step.
 *
 * Parameters:
 *  - String name: the name of the app / module, used internally
 *  - String type: 'npm' or 'react', dictates which deploy path to take
 *  - String prePublishCommand: a command to run before `npm publish`
 *      (optional, npm only)
 */
def call(body) {
    if (npmSkip()) {
        return;
    }

    Map config = [
            name: null,
            type: null,
    ]

    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    if (!config.name?.trim()) {
        throw new IllegalArgumentException('`name` parameter not set')
    }
    if (!config.type?.trim()) {
        throw new IllegalArgumentException('`type` parameter not set')
    }
    if (!['npm', 'react', 'react_native'].contains(config.type)) {
        throw new IllegalArgumentException('`type` must be one of `npm`, `react`,`react_native`')
    }

    currentBuild.displayName = jobDetails.prettyName()
    String dockerRegistry = '320083444745.dkr.ecr.us-west-2.amazonaws.com'
    String dockerRepo = "${dockerRegistry}/10e/builders"
    String dockerImage = "${dockerRepo}:${config.name}_${jobDetails.branchName().replaceAll(/[^a-zA-Z0-9-]/, '_')}"

    def ENV_BRANCHES = jobDetails.branchNameToAccountMap().keySet()

    Metrics metrics = metricsHelper.init('deployNpmModule')

    pipeline {
        agent {
            label 'generic'
        }
        options {
            buildDiscarder(logRotator(numToKeepStr: '10'))
            lock(resource: "${env.JOB_NAME}", inversePrecedence: true)
        }
        stages {
            stage('Initialize') {
                environment {
                    // This creates an NPM_CREDENTIALS_USR and NPM_CREDENTIALS_PSW credential with the username and password respecitvely
                    NPM_CREDENTIALS = credentials('/jenkins/artifactory')
                }
                steps {
                    script {
                        metrics.captureStage {
                            notify.started()

                            sshagent(['/jenkins/github.key']) {
                                sh '''
                                git submodule init
                                git submodule update --recursive --remote
                                '''
                            }

                            docker.withRegistry("https://${dockerRegistry}") {
                                def builtImage = docker.build(dockerImage,
                                        "--build-arg NPM_USER=${NPM_CREDENTIALS_USR} --build-arg NPM_PASS=${NPM_CREDENTIALS_PSW} .")
                                builtImage.push()
                                builtImage.push("${config.name}-latest")
                            }
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
                            echo "ios init $NPM_CREDENTIALS_PSW"
                        }
                    }
                    stage('android') {
                        agent {
                            node {
                                label 'shared'
                            }
                        }
                        steps {
                            sh "echo android init"
                        }
                    }
                }
            }
            stage('Build and Test') {
                steps {
                    script {
                        metrics.captureStage {
                            def image = docker.image(dockerImage)
                            image.pull()
                            image.inside {
                                sh '''
                                cd /app
                                yarn checkall
                                '''
                            }
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
                            echo 'ios init $$NPM_CREDENTIALS_PSW  $NPM_CREDENTIALS_PSW'
                        }
                    }
                    stage('android') {
                        agent {
                            node {
                                label 'shared'
                            }
                        }
                        steps {
                            sh "echo android init"
                        }
                    }
                }
            }
            stage('Deploy') {
                when {
                    allOf {
                        not {
                            expression {
                                return jobDetails.isPullRequest()
                            }
                        }
                        expression {
                            return jobDetails.branchName() == 'master'
                        }

                    }
                }
                steps {
                    script {
                        metrics.captureStage {
                            // def image = docker.image(dockerImage)
                            // image.pull()
                            //deploy RN web app here
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
                            //deploy iOS app here
                            echo "ios deploy ${NPM_CREDENTIALS_PSW}"
                        }
                    }
                    stage('android') {
                        agent {
                            node {
                                label 'shared'
                            }
                        }
                        steps {
                            //deploy android app here
                            echo "android deploy ${NPM_CREDENTIALS_PSW}"
                        }
                    }
                }
            }
        }
        post {
            failure {
                script {
                    notify.failure(metrics)
                }
            }
            success {
                script {
                    notify.success()
                }
            }
            always {
                script {
                    metrics.report()
                }
            }
        }

    }
}