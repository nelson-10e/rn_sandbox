pipeline {
  agent {
    docker {
      image 'runmymind/docker-android-sdk'
    }

  }
  stages {
    stage('Init') {
      parallel {
        stage('Init') {
          steps {
            echo 'Hello'
          }
        }
        stage('iOS') {
          steps {
            echo 'hello from iOS'
          }
        }
        stage('android') {
          steps {
            sleep 5
          }
        }
        stage('web') {
          steps {
            sh 'echo hello'
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
        stage('ios') {
          steps {
            sh 'printenv'
          }
        }
        stage('android') {
          steps {
            sh 'echo hello android'
          }
        }
        stage('web') {
          steps {
            sh 'echo hello'
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        node(label: 'shared')
      }
    }
  }
}