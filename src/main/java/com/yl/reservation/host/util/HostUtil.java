package com.yl.reservation.host.util;

import com.yl.reservation.host.exception.HostException;
import com.yl.reservation.host.model.ContactMethod;
import com.yl.reservation.host.model.Host;
import com.yl.reservation.host.model.HostRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class HostUtil {

    public static String getCurrentDateTimeString(){
        return LocalDateTime.now().toString();
    }

    public static void validateHostCreationFields(HostRequest request) throws HostException {
        validatePrimaryInfo(request);
        validateContactInfo(request);
    }

    public static void validatePrimaryInfo(HostRequest request){
        if ((request.getHost().getFirstName() == null || !StringUtils.hasText(request.getHost().getFirstName())))
            throw new HostException(HttpStatus.BAD_REQUEST,"First name is missing");
        if ((request.getHost().getLastName() == null || !StringUtils.hasText(request.getHost().getLastName())))
            throw new HostException(HttpStatus.BAD_REQUEST,"Last name is missing");
        if (request.getHost().getAddress() == null)
            throw new HostException(HttpStatus.BAD_REQUEST,"Address is missing");
    }

    public static void validateContactInfo(HostRequest request){
        boolean hasEmail = request.getHost().getEmail() != null && StringUtils.hasText(request.getHost().getEmail().get(0).getValue());
        boolean hasPhone = request.getHost().getPhone() != null && StringUtils.hasText(request.getHost().getPhone().get(0).getValue());
        boolean hasPrimaryContact;
        if ((hasEmail && hasPhone)) {
            hasPrimaryContact = (request.getHost().getPrimaryContactMethod() == ContactMethod.PHONE || request.getHost().getPrimaryContactMethod() == ContactMethod.EMAIL);
        } else {
            if (hasPhone) hasPrimaryContact = request.getHost().getPrimaryContactMethod() == ContactMethod.PHONE;
            else hasPrimaryContact = hasEmail && request.getHost().getPrimaryContactMethod() == ContactMethod.EMAIL;
        }

        boolean hasContactInfo = (hasEmail || hasPhone) && hasPrimaryContact;
        if (!hasContactInfo)
            throw new HostException(HttpStatus.BAD_REQUEST,"Contact information is missing");
    }

    public static boolean isUpdate(Host updatedHost, Host requestHost){
        if (requestHost.getEmail() != null && !requestHost.getEmail().equals(updatedHost.getEmail()))
            return true;
        if (requestHost.getPhone() != null && !requestHost.getPhone().equals(updatedHost.getPhone()))
            return true;
        if (requestHost.getBeds() != updatedHost.getBeds())
            return true;
        if (requestHost.getCrib() != null && !requestHost.getCrib().equals(updatedHost.getCrib()))
            return true;
        if (requestHost.getFullBath() != null && !requestHost.getFullBath().equals(updatedHost.getFullBath()))
            return true;
        if (requestHost.getPrivateEntrance() != null && !requestHost.getPrivateEntrance().equals(updatedHost.getPrivateEntrance()))
            return true;
        if (requestHost.getNotes() != null && !requestHost.getNotes().equals(updatedHost.getNotes()))
            return true;
        if (requestHost.getPrimaryContactMethod() != updatedHost.getPrimaryContactMethod())
            return true;
        return requestHost.getAddress() != null && !requestHost.getAddress().equals(updatedHost.getAddress());
    }

}
