package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.external.BillingServiceClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceClient billingServiceClient;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    @Value("${billing.service.url}")
    private String billingServiceUrl;
    public PatientService(PatientRepository patientRepository, BillingServiceClient billingServiceClient, RestTemplate restTemplate){
        this.patientRepository=patientRepository;
        this.billingServiceClient = billingServiceClient;
        this.restTemplate = restTemplate;
    }
    public List<PatientResponseDTO>getPatients(){
        List<Patient>patients=patientRepository.findAll();
        return patients.stream().map(patient-> PatientMapper.toDTO(patient)).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email already exists: "
                            + patientRequestDTO.getEmail());
        }

        // 1. Save patient
        Patient newPatient = patientRepository.save(
                PatientMapper.toModel(patientRequestDTO));

        log.info("Patient created with ID: {}", newPatient.getId());

        // 2. Create billing account — throws BillingServiceException if down
        billingServiceClient.createBillingAccount(
                newPatient.getId().toString(),
                newPatient.getName()
        );

        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
            throw new EmailAlreadyExistsException("A patient with this email already exists"+patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient=patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {

        patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Patient not found with ID: " + id));

        // Delete billing account first
        try {
            restTemplate.delete(
                    billingServiceUrl + "/api/billing-accounts/patient/" + id);
            log.info("Billing account deleted for patientId: {}", id);
        } catch (Exception e) {
            log.error("Could not delete billing account for patientId: {}", id);
        }

        patientRepository.deleteById(id);
    }

}
