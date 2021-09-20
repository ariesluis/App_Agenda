<?php
	//datos a guardar
	$nombre=$_REQUEST['nombre'];
	$apellido=$_REQUEST['apellido'];
	$clave=$_REQUEST['clave'];
	$correo=$_REQUEST['email'];
	//conexion
	$servidor="localhost";
	$usuario="root";
	$contrasenia="";
	$basedatos="db_agenda";

	$conexion = mysqli_connect($servidor, $usuario, $contrasenia, $basedatos);

	//chequeo de la conexion
	if ($conexion->connect_error) {
    	die("Connection failed: " . mysqli_connect_error());
	} 

	$sql="Insert into usuario (nombre, apellido, clave, correo) values ('$nombre', '$apellido', MD5('$clave'), '$correo')";
	if (mysqli_query($conexion, $sql)) {
		echo json_encode(1);
	}
	else
	{
		echo json_encode(0);
	}
	mysqli_close($conexion);
?>