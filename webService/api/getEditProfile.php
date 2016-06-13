<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_POST['api_key'];
$nama=$_POST['nama'];
$alamat=$_POST['alamat'];
$tlp=$_POST['tlp'];
$line=$_POST['line'];
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$data_id=mysql_fetch_array($query);
	$data_id['id_user'];
	$query=mysql_query("UPDATE user SET nama='".$nama."',alamat='".$alamat."',telepon='".$tlp."',id_line='".$line."' WHERE id_user='".$data_id['id_user']."'");
	if($query){
		$obj['Nama']=$nama;
		$obj['Alamat']=$alamat;
		$obj['Tlp']=$tlp;
		$obj['Line']=$line;
		$obj['Error']="False";
	}
	else{
			$obj['Error']="True";
	}
}
echo json_encode($obj);
?>