package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialService {

    private final CredentialMapper credentialsMapper;

    public CredentialService(CredentialMapper credentialsMapper) {
        this.credentialsMapper = credentialsMapper;
    }

    public List<Credential> getCredentialsByUserId(Integer userId) {
        return credentialsMapper.getCredentialsByUserId(userId);
    }

    public Integer saveCredential(Credential credential) {
        Integer credentialId = credential.getCredentialId();
        if (credentialId == null) {
            credentialId = credentialsMapper.saveCredential(credential);
        } else {
            credentialsMapper.updateCredential(credential);
        }
        return credentialId;
    }

    public int deleteCredential(Integer credentialId, Integer userId) {
        return credentialsMapper.deleteCredential(credentialId, userId);
    }

    public Credential getCredentialById(Integer credentialId) {
        return credentialsMapper.getCredentialById(credentialId);
    }
}
