<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
// Path to move uploaded files
$target_path = "../uploads/";
 
// array for final json respone
$response = array();
 
// getting server ip address
$server_ip = gethostbyname(gethostname());
 
// final file url that is being uploaded
$file_upload_url = "http://mayerscartensz.esy.es/uploads/";

date_default_timezone_set("Asia/Jakarta");
$date = date("Y-m-d");
$time = date("H:i:s");
$subject=$_POST['sub'];
$bahan=$_POST['bahan'];
$jenis=$_POST['jenis'];
$jumlah=$_POST['jumlah'];
$token=$_POST['api_key'];
$keterangan=$_POST['ket'];

$response['subject'] = $subject;
$response['bahan'] = $bahan;
$response['jenis'] = $jenis;
$response['jumlah'] = $jumlah;
$response['token'] = $token;
$response['keterangan'] = $keterangan;

$query=mysql_query("SELECT id_user from user where token='".$token."'");
$data=mysql_fetch_array($query);
$response['id'] = $data['id_user'];
if (isset($_FILES['image']['name'])) {
	$filename= $token.str_replace("-","",$date).str_replace(":","",$time).basename($_FILES['image']['name']);
	$target_path = $target_path . $filename;
    try {
        // Throws exception incase file is not being moved
        if (!move_uploaded_file($_FILES['image']['tmp_name'], $target_path)) {
            // make error flag true
            $response['error'] = true;
            $response['message'] = 'Could not move the file!';
        }
		else{
			// File successfully uploaded
			$response['message'] = 'File uploaded successfully!';
			$response['error'] = false;
			$file_url=$file_upload_url . $filename;
			$response['file_path'] = $file_url;
		}
    } catch (Exception $e) {
        // Exception occurred. Make error flag true
        $response['error'] = true;
        $response['message'] = $e->getMessage();
    }
}

$query=mysql_query("INSERT INTO orders (id_user,subject,jenis,bahan,jumlah,keterangan,desain,alamat_kirim,harga,status,progres,tgl_order,jam_order) VALUES ('".$data['id_user']."','".$subject."','".$jenis."','".$bahan."','".$jumlah."','".$keterangan."','".$file_url."\n','notset','0','0','Menunggu konfirmasi harga oleh admin','".$date."','".$time."')");
// Echo final json response to client
if($query){
}else{
	$response['error']=true;
	$response['message'] = mysql_error();
}
echo json_encode($response);
?>