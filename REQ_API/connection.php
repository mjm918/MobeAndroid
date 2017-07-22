<?php
    $host = "localhost";
    $user = "id2254050_job";
    $pass = "julfikar123!";
    $database = "id2254050_akash";

    try{
        $con = mysqli_connect($host,$user,$pass,$database) or die("Database Connection Error");
    }catch(Exception $e){
        echo $e;
    }
?>