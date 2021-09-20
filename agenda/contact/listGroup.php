<?php
	$id=$_REQUEST['id'];

	$servidor="localhost";
	$usuario="root";
	$contrasenia="";
	$basedatos="db_agenda";

	$conexion =new mysqli($servidor, $usuario, $contrasenia, $basedatos);
	$res=$conexion->query("Select * from grupo_contacto where id_contacto=$id");
	$datos= array();
	foreach ($res as $row) {
		$datos[]=$row;
	}
	echo json_encode($datos);
	$conexion->close();
?>