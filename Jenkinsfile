#!groovy
node {
    stage('Checkout') {
        echo 'Checkout'
        checkout scm
    }

    stage('Build') {
        echo 'Build Neo-Starters'
        mvn 'clean package -DskipTests -B -e -V'
    }

    stage('Test') {
        mvn 'test -B -e -V'
    }

    stage('Sonar') {
        if ('development'.equalsIgnoreCase(env.BRANCH_NAME)) {
            echo 'Development branch - running regular Sonar'
            mvn 'sonar:sonar -B -e -V'
        } else if (env.CHANGE_ID != null) {
            echo 'Pull request - running Sonar preview'
            mvn "sonar:sonar -Dsonar.github.pullRequest=${env.CHANGE_ID} -Dsonar.github.repository=neoteric-eu/neo-starters -Dsonar.github.oauth=${env.SONAR_OAUTH} -Dsonar.analysis.mode=issues -B -e -V"
        } else {
            echo 'Should not be here'
        }
    }

    if ('development'.equalsIgnoreCase(env.BRANCH_NAME)) {
        stage('Deploy SNAPSHOT') {
            echo 'Deploy SNAPSHOT'
            configFileProvider(
                    [configFile(fileId: '1d4fc1ee-2ac5-4b80-aec2-e1591a34bb9b', variable: 'MAVEN_SETTINGS')]) {
                mvn 'deploy -s $MAVEN_SETTINGS -B -e -V -DskipTests -PreleaseStarters'
            }
        }
    }
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}
