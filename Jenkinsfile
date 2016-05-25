#!groovy
node {
    stage 'Checkout'
    echo 'Checkout'
    checkout scm

    stage 'Build'
    echo 'Build Neo-Starters'
    mvn 'clean package -DskipTests -B -e -V'

    stage 'Test'
    mvn 'test -B -e -V'

    stage 'Sonar'
    if ('development'.equalsIgnoreCase(env.BRANCH_NAME)) {
        echo 'Development branch - running regular Sonar'
        mvn 'sonar:sonar -B -e -V'
    } else if (env.CHANGE_ID != null) {
        echo 'Pull request - running Sonar preview'
        mvn "sonar:sonar -Dsonar.github.pullRequest=${env.CHANGE_ID} -Dsonar.github.repository=neoteric-eu/neo-starters -Dsonar.github.oauth=78725005a8853b841aa554e806c7be53a8158420 -Dsonar.analysis.mode=issues -B -e -V"
    } else {
        echo 'Should not be here'
    }

    if ('development'.equalsIgnoreCase(env.BRANCH_NAME)) {
        stage 'Deploy SNAPSHOT'
        echo 'Deploy SNAPSHOT'
        mvn 'deploy -B -e -V'
    }
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}
