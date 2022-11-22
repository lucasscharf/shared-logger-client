# shared-logger-client

## Como rodar
Assume-se que as máquinas já tenham instalado Java e Maven.
Rodar o script initialization_script.sh. Esse script vai instalar o docker, clonar o código dos serviços e compilar tudo.
Dependendo de como estiver configurada a máquina, pode ser necessário alguma intervenção manual após criar o usuário docker.

Após compilar os serviços, rodar os seguintes comandos em máquinas separadas. Assume-se que o IP da máquina do zookeeper é 10.10.1.1. Caso seja um IP diferente, é necessário alterar o código:

Levantar o zookeeper:
```bash
cd ~/shared-logger-client; docker-compose up -d zookeeper
```
 
Rodar o proposer:
```bash
cd ~/shared-logger-client/URingPaxos/target/Paxos-trunk ; ./thriftnode.sh 1 1:PA 10.10.1.1
```

Rodar o acceptor:
```bash
cd ~/shared-logger-client/URingPaxos/target/Paxos-trunk ;   ./ringpaxos.sh 2 1:A 10.10.1.1
```

Rodar a réplica (serviço do smr):
```bash
cd ~/shared-logger-client/URingPaxos/clients/SMR/target/SMR-trunk ;  ./replica.sh 1:L,4,0 0 10.10.1.1
```

Rodar o serviço de réplicas log e criar um log nele:
```bash
cd ~/shared-logger-client/server
mvn quarkus:dev &
sleep 10
curl -X POST "http://localhost:8888/register" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"id\":4,\"pathPrefix\":\"/tmp/\",\"replicas\":2,\"ring\":\"1\"}"
fg
```

Rodar o cliente. No caso, o comando levanta 10 threads, que enviam 100 comandos com tanho de 1kb usando o zookeeper na máquina 10.10.1.1.

```bash
~/shared-logger-client/run_experiment.sh 10 100 1024 10.10.1.1
```
## Para impedir o processo de recovery nas instâncias, executar o seguinte comando
Executar o comando: 

```bash
/opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology1/config/learner_recovery 0
/opt/zookeeper/bin/zkCli.sh set /ringpaxos/topology2/config/learner_recovery 0
```

Dentro da máquina docker do zookeeper
