package rs.raf.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.raf.backend.dto.ErrorMessageDTO;
import rs.raf.backend.model.ErrorMessage;
import rs.raf.backend.model.User;
import rs.raf.backend.model.UserTypes;
import rs.raf.backend.repository.ErrorMessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErrorMessageService {
    private final ErrorMessageRepository errorMessageRepository;
    private final UserService userService;

    public ErrorMessageService(ErrorMessageRepository errorMessageRepository, UserService userService) {
        this.errorMessageRepository = errorMessageRepository;
        this.userService = userService;
    }

    public Page<ErrorMessageDTO> getErrors(Pageable pageable) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(username);

        Page<ErrorMessage> errorMessages;
        if (currentUser.getRole().equals(UserTypes.ADMIN)) {
            errorMessages = errorMessageRepository.findAll(pageable);
        } else {
            errorMessages = errorMessageRepository.findByOrderCreatedBy(currentUser, pageable);
        }

        List<ErrorMessageDTO> errorMessageDTOs = errorMessages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(errorMessageDTOs, pageable, errorMessages.getTotalElements());
    }

    private ErrorMessageDTO convertToDTO(ErrorMessage errorMessage) {
        ErrorMessageDTO dto = new ErrorMessageDTO();
        dto.setId(errorMessage.getId());
        dto.setDate(errorMessage.getDate());
        dto.setOperation(errorMessage.getOperation());
        dto.setMessage(errorMessage.getMessage());
        dto.setOrderId(errorMessage.getOrder().getId());
        return dto;
    }
}