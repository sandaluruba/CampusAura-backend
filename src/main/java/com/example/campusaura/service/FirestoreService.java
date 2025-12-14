package com.example.campusaura.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    // Create or Update
    public String saveDocument(String collection, String documentId, Map<String, Object> data) 
            throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> result = firestore.collection(collection)
                .document(documentId)
                .set(data);
        return result.get().getUpdateTime().toString();
    }

    // Read
    public Map<String, Object> getDocument(String collection, String documentId) 
            throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(collection)
                .document(documentId)
                .get()
                .get();
        return document.exists() ? document.getData() : null;
    }

    // Delete
    public String deleteDocument(String collection, String documentId) 
            throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> result = firestore.collection(collection)
                .document(documentId)
                .delete();
        return result.get().getUpdateTime().toString();
    }

    public String saveUser(String uid, Map<String, Object> userObject) 
            throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> result = firestore.collection("users")
                .document(uid)
                .set(userObject);
        return result.get().getUpdateTime().toString();
}

    // Get all documents
    public List<QueryDocumentSnapshot> getAllDocuments(String collection) 
            throws ExecutionException, InterruptedException {
        return firestore.collection(collection).get().get().getDocuments();
    }
}