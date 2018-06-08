pipeline {
  agent any
  stages {
    stage('Init') {
      parallel {
        stage('Init') {
          steps {
            echo 'Hello'
          }
        }
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
        stage('Build') {
          steps {
            script {
              sh "echo hi from script"
            }

          }
        }
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
            sh 'echo hello android'
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