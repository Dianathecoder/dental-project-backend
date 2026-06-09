package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.model.patient.Document;
import com.dynalar.dynalar.model.patient.Patient;
import com.dynalar.dynalar.respository.DocumentRepository;
import com.dynalar.dynalar.respository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PatientRepository patientRepository;

    private final Path rootPath = Paths.get("uploads");

    @PostMapping(value = "/patient/{patientId}/upload", consumes = "multipart/form-data")
    public ResponseEntity<Document> uploadFile(
            @PathVariable Long patientId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {

        Patient patient = patientRepository.findById(patientId).orElse(null);
        
        if (patient == null) {
        	return ResponseEntity.notFound().build();
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String lowerCaseName = originalName.toLowerCase();
        
        if (!lowerCaseName.endsWith(".pdf") && !lowerCaseName.endsWith(".jpg") && 
            !lowerCaseName.endsWith(".jpeg") && !lowerCaseName.endsWith(".png")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build(); 
        }
        
        try {
            Document doc = new Document();
            doc.setPatient(patient);
            doc.setType(type);
            doc.setCreationDate(LocalDateTime.now());
            doc.setName(file.getOriginalFilename());
            doc = documentRepository.save(doc);

            String relativePath = savePhysicalFile(file, patientId, doc.getId());

            doc.setDocumentUrl(relativePath);
            documentRepository.save(doc);

            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    

    @GetMapping("/{documentId}")
    public ResponseEntity<Resource> getDocument(@PathVariable Long documentId) {
        Document doc = documentRepository.findById(documentId).orElse(null);
        if (doc == null || doc.getDocumentUrl() == null) return ResponseEntity.notFound().build();

        try {
            Path filePath = rootPath.resolve(doc.getDocumentUrl());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Document>> getPatientDocuments(@PathVariable Long patientId) {
        return ResponseEntity.ok(documentRepository.findByPatientId(patientId));
    }

    
    @PutMapping("/{documentId}/name")
    public ResponseEntity<Document> updateDocumentName(
            @PathVariable Long documentId,
            @RequestParam("newName") String newName) {
            
        Document doc = documentRepository.findById(documentId).orElse(null);
        
        if (doc == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
        }

        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        String originalName = doc.getName();
        String extension = "";
        
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        
        String finalName = newName.trim();
        if (finalName.contains(".")) {
            finalName = finalName.substring(0, finalName.lastIndexOf("."));
        }
        doc.setName(finalName + extension);
        documentRepository.save(doc);
        return ResponseEntity.ok(doc);
    }
    
    
    private String savePhysicalFile(MultipartFile file, Long patientId, Long docId) throws IOException {
        String patientFolder = "patient_" + patientId;
        Path targetFolder = this.rootPath.resolve(patientFolder);

        if (!Files.exists(targetFolder)) {
            Files.createDirectories(targetFolder);
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = docId + extension;
        Path targetPath = targetFolder.resolve(fileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return patientFolder + "/" + fileName;
    }
    
    
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        Document doc = documentRepository.findById(documentId).orElse(null);
  	  	if (doc == null) {
			return ResponseEntity.notFound().build();
		}
    
        try {
            if (doc.getDocumentUrl() != null) {
                Path filePath = rootPath.resolve(doc.getDocumentUrl());
                Files.deleteIfExists(filePath);
            }
            documentRepository.deleteById(documentId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}