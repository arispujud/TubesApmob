<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_GET['api_key'];
$response = array();
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$data_id=mysql_fetch_array($query);
	$data_id['id_user'];
	$query=mysql_query("UPDATE orders SET status='2', progres='Silahkan lakukan konfirmasi pembayaran' WHERE id_user='".$data_id['id_user']."' AND status='1'");
	$query=mysql_query("SELECT * FROM rekening");
	$i=0;
	while($data=mysql_fetch_array($query)){
		$obj['id_rekening']=$data['id_order'];
		$obj['noRek']=$data['no_rek'];
		$obj['namaRek']=$data['nama_rek'];
		$obj['kantorRek']=$data['kantor'];
		$obj['urlRek']=$data['logo'];
		$response['data'][$i]=$obj;
		$i++;
	}
}
echo json_encode($response);
?>