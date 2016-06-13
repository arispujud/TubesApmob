<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_POST['api_key'];
$lama=$_POST['lm'];
$baru=$_POST['br'];
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$data_id=mysql_fetch_array($query);
	$data_id['id_user'];
	if(md5($lama)==$data_id['password']){
			$query=mysql_query("UPDATE user SET password='".md5($baru)."' WHERE id_user='".$data_id['id_user']."'");
			if($query){
				$obj['Error']="False";
			}
	}
	else{
			$obj['Error']="True";
	}
}
echo json_encode($obj);
?>