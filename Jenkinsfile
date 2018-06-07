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
      }
    }
    stage('Build') {
      steps {
        script {
          sh "echo hi from script"
        }

      }
    }
  }
}