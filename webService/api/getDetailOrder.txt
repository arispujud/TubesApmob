<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_GET['api_key'];
$response = array();
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$data_id=mysql_fetch_array($query);
	$data_id['id_user'];
	$query=mysql_query("SELECT * FROM orders where id_user='".$data_id['id_user']."' ORDER BY id_order DESC");
	$i=0;
	while($data=mysql_fetch_array($query)){
		$obj['id_order']=$data['id_order'];
		$obj['id_user']=$data['id_user'];
		$obj['subject']=$data['subject'];
		$obj['progres']=$data['progres'];
		$obj['status']=$data['status'];
		$obj['harga']=$data['harga'];
		$obj['tgl']=$data['tgl_order'];
		$obj['jam']=$data['jam_order'];
		$response['data'][$i]=$obj;
		$i++;
	}
}
echo json_encode($response);
?>