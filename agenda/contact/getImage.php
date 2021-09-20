<?php
	$id=$_REQUEST['id'];

	$servidor="localhost";
	$usuario="root";
	$contrasenia="";
	$basedatos="db_agenda";

	$conexion =new mysqli($servidor, $usuario, $contrasenia, $basedatos);
	$res=$conexion->query("Select * from imagen where contacto_id_contacto=$id");
	$datos = array();
	foreach ($res as $row) {
		$datos[]=$row;
	}
	/*
	while ( $row = $res->fetch_assoc()) {
		$datos[]=$row["id_imagen"];
		$datos[]=utf8_encode($row["comentario"]);
		$datos[]=$row["imagen"];
		$datos[]=$row["contacto_id_contacto"];
	}*/
	echo json_encode(utf8ize($datos));
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