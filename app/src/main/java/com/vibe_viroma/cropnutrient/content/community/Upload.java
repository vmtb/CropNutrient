package com.vibe_viroma.cropnutrient.content.community;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class Upload {
    String path, extension;
    private static uploadingListener listener;


    public void setListener(uploadingListener listener) {
        Upload.listener = listener;
    }

    public Upload(String path, String extension){
        this.path= path;
        this.extension= extension;
    }

    public void makeUplaod(){
        Uri file = Uri.fromFile(new File(path));
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference riversRef = mStorageRef.child("medias/community/"+(new Date()).getTime()+"."+extension);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                listener.onSuccess(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        listener.onProgressListener((int) (taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()));
                    }
                });
    }

    public interface uploadingListener{
        void onProgressListener(int percentage);
        void onSuccess(String link);
    }
}
