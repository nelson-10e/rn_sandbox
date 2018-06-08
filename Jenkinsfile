pipeline {
  agent any
  stages {
    stage('init') {
      agent {
        node {
          label 'shared'
        }

      }
      steps {
        script{
        sh "echo init"
        }
      }
    }
    stage('build_deploy') {
      agent {
        node {
          label 'shared'
        }

      }
      steps {
        script{
            def image = docker.image("runmymind/docker-android-sdk:alpine-lazydl")
            image.pull()
            image.inside {
                sh '''
                ls
                echo "hello from sdk"
                '''
            }
        }
      }
    }
  }
}