<?php
	error_reporting(E_ALL^E_NOTICE);
	include "koneksi.php";
	$email=$_POST['email'];
	$password=$_POST['password'];
	
	$password=md5($password);
	
	$query=mysql_query("SELECT * FROM user where email='".$email."'");
	
	if(mysql_num_rows($query)>0){
		$data=mysql_fetch_array($query);
		if($password==$data['password']){
			//echo "{\"Status\": \"Success\",\"Token\":\"".$data['token']."\",\"Id\": \"".$data['id_user']."\",\"Name\": \"".$data['nama']."\",\"Email\": \"".$data['email']."\"}";
			$obj['Status']="Success";
			$obj['Token']=$data['token'];
			$obj['Id']=$data['id_user'];
			$obj['Name']=$data['nama'];
			$obj['Email']=$data['email'];
			$obj['Alamat']=$data['alamat'];
			$obj['Tlp']=$data['telepon'];
			$obj['Line']=$data['id_line'];
			echo json_encode($obj);
		}
		else{
			echo "{\"Status\": \"Failed\",\"Token\":\"Failed\"}";
		}
	}
	else{
		echo "{\"Status\": \"Failed\",\"Token\":\"Failed\"}";
	}
	
?>