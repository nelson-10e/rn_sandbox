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
          agent any
          steps {
            echo 'ios Test And Build'
          }
        }
        stage('android') {
          agent {
            docker {
              image 'runmymind/docker-android-sdk'
            }

          }
          steps {
            echo 'android init'
          }
        }
        stage('web') {
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
          agent any
          steps {
            echo 'ios Test And Build'
          }
        }
        stage('android') {
          agent {
            docker {
              image 'runmymind/docker-android-sdk'
            }

          }
          steps {
            sh 'echo hello android'
          }
        }
        stage('web') {
          agent any
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
          agent any
          steps {
            echo 'iOS Deploy'
          }
        }
        stage('android') {
          agent {
            docker {
              image 'runmymind/docker-android-sdk'
            }

          }
          steps {
            echo 'android deploy'
          }
        }
        stage('web') {
          steps {
            echo 'web deploy'
          }
        }
      }
    }
  }
}