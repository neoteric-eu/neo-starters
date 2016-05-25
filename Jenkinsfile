node {
    stage 'Checkout'
    sh 'env > env.txt'
    readFile('env.txt').split("\r?\n").each {
        println it
    }
    checkout scm

    stage 'Build'
     mvn 'clean package -DskipTests'

    stage 'Test'
     mvn 'test'

    stage 'Sonar'
    mvn 'sonar:sonar'

    stage 'Deploy SNAPSHOT'
    mvn 'deploy'
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}