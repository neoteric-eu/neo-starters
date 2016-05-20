node {
    stage 'Checkout'
    checkout scm

    stage 'Build'
     mvn 'clean compile'

    stage 'Test'
     mvn 'test'

    stage 'Code quality'
    echo 'Mocked Sonar for now'
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}