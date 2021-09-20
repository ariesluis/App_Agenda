<?php
	header('Content-type : bitmap; charset=utf-8');
	////////////////////////////////////////
	//datos a guardar
	$array1=json_decode($_REQUEST['jsonContacto']);
	$array2=json_decode($_REQUEST['jsonGrupo']);
	$codigo_imagen = $_REQUEST['codigo_imagen'];
	////////////////////////////////////////
	//conexion
	$servidor="localhost";
	$usuario="root";
	$contrasenia="";
	$basedatos="db_agenda";
	////////////////////////////////////////
	//contacto
	$nombre="";$telefono="";$direccion="";$alias="";$id_usuario=0;
	//imagen
	$imagen=[]; $comentario="";
	//lectura de json
    $nombre = $array1->nombre;
    $telefono=$array1->telefono;
    $direccion=$array1->direccion;
    $alias=$array1->alias;
    $id_usuario=$array1->id_usuario;
    $imagen=$array1->imagen;
    $comentario=$array1->comentario;

	/////////////////////////////////////////////////////////////
	//operaciones db
	$sql_contact="Insert into contacto (nombre, telefono, direccion, alias, usuario_id_usuario) values ('$nombre','$telefono','$direccion','$alias',$id_usuario)";
	$conexion = new mysqli($servidor, $usuario, $contrasenia, $basedatos);
	if ($conexion->connect_error) {
    	die("Connection failed: " . $conexion->connect_error);
	}

	if ($conexion->query($sql_contact)===TRUE) {
		//id del contacto guardado
		$last_id = $conexion->insert_id;
		//subida de la imagen al servidor
		uploadImage($last_id, $codigo_imagen);
		//insercion de la imagen
		$sql_imagen="Insert into imagen (imagen, comentario, contacto_id_contacto) values ('$imagen','$comentario',$last_id)";
		$conexion->query($sql_imagen);
		//insercion contacto_grupo
		foreach ($array2 as $key => $value) {
			$sql_grupo="Select id_grupo from grupo where nombre='$value'";
			$id_grupo=$conexion->query($sql_grupo)->fetch_assoc();
			if ($id_grupo['id_grupo']>0) {
				$idg=intval($id_grupo['id_grupo']);
				$sql_grupo_contacto="Insert into grupo_contacto (id_grupo, id_contacto) values ($idg, $last_id)";
				$conexion->query($sql_grupo_contacto);
			}
		}
	}
	$conexion->close();

	function uploadImage($id_contacto, $codigo_imagen)
	{
		$path = '../upload/'.$id_contacto.'.jpg';
		$decode_imagen = base64_decode($codigo_imagen);

		$file  = fopen($path, 'wb');
		$is_written = fwrite($file, $decode_imagen);
		fclose($file);
	}
?>