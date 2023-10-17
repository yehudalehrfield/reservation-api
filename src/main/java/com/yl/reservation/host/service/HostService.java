package com.yl.reservation.host.service;

import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.repository.HostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HostService {

    @Autowired
    HostRepository hostRepository;

    public Host createHost(Host host){
        if (isExistingHost(host)) return null;
        return hostRepository.insert(host);
    }

    public List<Host> getHosts(){
        return hostRepository.findAll();
    }

    public Optional<Host> deleteHost(String id) {
        Optional<Host> deletedHost = hostRepository.findById(id);
        hostRepository.deleteById(id);
        return deletedHost;

    }

    public boolean isExistingHost(Host host){
        Optional<Host> existingHost = hostRepository.findByLastNameAndAddress(host.getLastName(), host.getAddress());
        return existingHost.isPresent();
    }
}
