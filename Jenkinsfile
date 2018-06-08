pipeline {
  agent any
  stages {
    stage('init') {
      agent {
        node {
          label 'ios'
        }

      }
      steps {
        echo 'ios Test And Build'
      }
    }
    stage('build_deploy') {
      agent {
        node {
          label 'shared'
        }

      }
      steps {
        sh 'echo hello android'
      }
    }
  }
}