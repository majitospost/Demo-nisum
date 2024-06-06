# Demo project
## Tecnología
- Java 17
- Gradle
- H2
## Ejecutando el proyecto

### Instalando Java 17
#### Usando SDKMAN
```shell
curl -s "https://get.sdkman.io" | bash
sdk version
```
#### Instalando Java 17
```shell
sdk install java 17.0.11-amzn
sdk use java 17.0.11-amzn   #si se desea usar localmente
sdk default java   #si se desea usar localmente
java -version
```

### Clonando el proyecto
```shell
git clone https://github.com/majitospost/Demo-nisum.git
```
### Instalando dependencias
```shell
./gradlew build
```
### Ejecutando el proyecto
```shell
./gradlew bootRun
```
> [!NOTA]
> No es necesario crear la base de datos, esta se creará automáticamente cuando se ejecute el programa.

## APIs
Para llamar los API por favor usar los siguientes comandos cURL:
### Creando usuario
```shell
curl --request POST \
  --url http://localhost:8080/api/users \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "Juan Rodriguez",
	"email": "juan@rodriguez.org",
	"password": "hunter2",
	"phones": [
		{
			"number": "1234567",
			"cityCode": "1",
			"countryCode": "57"
		}
	]
}'
```
### Modificando usuario
```shell
curl --request PUT \
  --url http://localhost:8080/api/users \
  --header 'Content-Type: application/json' \
  --data '{
	"id": "{{userId}}",
	"name": "Modificado",
	"email": "modificado@mail.org",
	"password": "password123",
	"phones": [
		{
			"id": 2,
			"number": "56789",
			"cityCode": "34",
			"countryCode": "789"
		}
	]
}'
```
> [!NOTA]
> Reemplazar {{userId}} por el UUID que devuelve el API anterior o el que se obtenga en el API GET

### Listando usuarios
```shell
curl --request GET \
  --url http://localhost:8080/api/users
```

### Eliminando usuario
```shell
curl --request DELETE \
  --url http://localhost:8080/api/users/{{userId}}
```
> [!NOTA]
> Reemplazar {{userId}} por el UUID del usuario a eliminar

# Open API (Swagger)
Abrir el siguiente [enlace](http://localhost:8080/api/swagger-ui/index.html) para ver la UI de Swagger.

# Pruebas unitarias
Hay dos archivos de pruebas, uno para el servicio y otro para el controlador. Para ejecutar las pruebas usar los siguientes comandos:
```shell
./gradlew clean
./gradlew test
```

# Diagramas de secuencia
Se realizó cuatro diagramas de secuencia, uno por cada operación CRUD de usuarios.
## Guardar y modificar
![](guardar-modificar.png)

## Eliminar y listar
![](eliminar-listar.png)
