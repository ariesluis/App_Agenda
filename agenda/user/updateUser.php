<?php
	$id=$_REQUEST['id'];
	$nombre=$_REQUEST['nombre'];
	$apellido=$_REQUEST['apellido'];
	$clave = $_REQUEST['clave'];
	$email=$_REQUEST['email'];

	$servidor="localhost";
	$usuario="root";
	$contrasenia="";
	$basedatos="db_agenda";

	$longitud_clave = strlen($clave);
	$query="";
	if ($longitud_clave>=32) {
		$query="Update usuario set nombre='$nombre', apellido='$apellido', correo='$email' where id_usuario=$id";
	}
	else
	{
		$query="Update usuario set nombre='$nombre', apellido='$apellido', clave=MD5('$clave'), correo='$email' where id_usuario=$id";
	}
	$conexion =new mysqli($servidor, $usuario, $contrasenia, $basedatos);
	if ($conexion->query($query)===TRUE) 
	{
	 	echo json_encode(1);
	}
	 else
	{
		echo json_encode(0);
	}
	
	$conexion->close();
?>