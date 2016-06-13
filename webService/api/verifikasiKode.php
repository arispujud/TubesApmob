<?php
	error_reporting(E_ALL^E_NOTICE^E_WARNING);
	include "koneksi.php";
	$email = $_POST['email'];
	$kode = $_POST['kd'];
	
	$query=mysql_query("SELECT * FROM user where email='".$email."'");
	$data=mysql_fetch_array($query);
	if($data['kode']==$kode){
			$obj['Error']="False";
			$obj['Token']=$data['token'];
	}
	else{
		$obj['Error']="True";
	}
	echo json_encode($obj);
?>