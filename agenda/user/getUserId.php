<?php
	$id=$_REQUEST['id'];

	$servidor="localhost";
	$usuario="root";
	$contrasenia="";
	$basedatos="db_agenda";

	$conexion =new mysqli($servidor, $usuario, $contrasenia, $basedatos);
	$res=$conexion->query("Select * from usuario where id_usuario=$id");
	$datos= array();
	foreach ($res as $row) {
		$datos[]=$row;
	}
	echo json_encode( utf8ize($datos));
	$conexion->close();

	function utf8ize($d) {
	    if (is_array($d)) {
	        foreach ($d as $k => $v) {
	            $d[$k] = utf8ize($v);
	        }
	    } else if (is_string ($d)) {
	        return utf8_encode($d);
	    }
	    return $d;
	}
?>