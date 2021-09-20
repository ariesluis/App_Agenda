<?php
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//datos a guardar
	$arrayC=json_decode($_REQUEST['jsonContacto']);
	$arrayG=json_decode($_REQUEST['jsonGrupo']);
	$arrayI=json_decode($_REQUEST['jsonImagen']);
	$codigo_imagen = $_REQUEST['codigo_imagen'];
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	$nombre=$arrayC->nombre;
	$telefono=$arrayC->telefono;
	$direccion=$arrayC->direccion;
	$alias=$arrayC->alias;
	$id_contacto=$arrayC->id_contacto;
	//imagen
	$imagen=$arrayI->imagen;
	$comentario=$arrayI->comentario;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	$servidor="localhost";
	$usuario="root";
	$contrasenia="";
	$basedatos="db_agenda";
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	$conexion =new mysqli($servidor, $usuario, $contrasenia, $basedatos);
	if ($conexion->query("Update contacto set nombre='$nombre', telefono='$telefono', direccion='$direccion', alias='$alias' where id_contacto=$id_contacto")===TRUE) 
	{
	 	if ($conexion->query("Update imagen set imagen='$imagen', comentario='$comentario' where contacto_id_contacto=$id_contacto")===TRUE) {
	 		uploadImage($id_contacto, $codigo_imagen);
	 		if ($conexion->query("Delete from grupo_contacto where id_contacto='$id_contacto'")===TRUE) {
	 			foreach ($arrayG as $key => $value) {
	 				$id_grupo=$conexion->query("Select id_grupo from grupo where nombre='$value'")->fetch_assoc();
	 				if ($id_grupo['id_grupo']>0) {
	 					$idg=intval($id_grupo['id_grupo']);
						$conexion->query("Insert into grupo_contacto (id_grupo, id_contacto) values ($idg, $id_contacto)");
					}
	 			}
	 		}
	 	}
	 	else
	 	{
	 		echo "Fallo actualizando la imagen";
	 	}
	}
	 else
	{
		echo "Fallo actualizando el contacto";
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