package com.ecom.EcomSB.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File name of the current/original file
        String originalFileName = file.getOriginalFilename();

        // Generate a unique file name
        String randomID = UUID.randomUUID().toString();
        // basic explination ::: filename = abcd.jpg -> UUID = 1234 --> your file name will be(fileName) 1234.jpg
        String fileName = randomID.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));

        // explination :: path that we send from method ("/imagedir") + File.separator(ye ek "/" add kar dega beeche me vaise to hum direct "/" bhi likh sakte the lekin vo only ek specific OS per hi kaam karta hai jaise windows per "/" chal jaata hai lekin  linux per "/" kaam nahi karta isliye inbuilt functionality use kar li) + fileName(yaha per UUID + extension aa rhi hai humaare pass)
        String filePath = path + File.separator + fileName;

        // Check if path exist and create
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        // upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath)); // getInputStream give you error so we need to add a IOException in method

        // return file name
        return fileName;

    }
}
