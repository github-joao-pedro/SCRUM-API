package api.scrum.user.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.scrum.exceptions.BusinessException;
import api.scrum.project.view.ProjectBaseView;
import api.scrum.relation_user_project.repository.RelationUserProjectRepository;
import api.scrum.user.model.User;
import api.scrum.user.repository.UserRepository;
import api.scrum.user.service.base.UserService;
import api.scrum.user.view.UserBaseView;
import api.scrum.user.view.UserView;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationUserProjectRepository relationUserProjectRepository;

    private ModelMapper modelMapper;

    // Definindo mensagens de erro como constantes
    private static final String USER_NOT_FOUND_MESSAGE = "Usuário não encontrado";
    private static final String INVALID_PARAMETERS_MESSAGE = "Parâmetros inválidos";
    private static final String ID_REQUIRED_MESSAGE = "ID obrigatório";

    @Override
    public UserBaseView create(UserView userView) {
        modelMapper = new ModelMapper();
        
        // Validação dos parâmetros do usuário
        validateUserView(userView);

        // Mapeia o UserView para a entidade User e salva no banco de dados
        User user = modelMapper.map(userView, User.class);
        return modelMapper.map(userRepository.save(user), UserBaseView.class);
    }

    @Override
    public UserBaseView update(UserView userView) {
        modelMapper = new ModelMapper();
        
        // Verifica se o ID é nulo e lança uma exceção se for
        if (userView.getId() == null) {
            throw new BusinessException(ID_REQUIRED_MESSAGE);
        }

        // Busca o usuário existente no banco de dados
        User existingUser = userRepository.findById(userView.getId())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
    
        // Atualiza apenas os campos que foram fornecidos na requisição
        if (userView.getEmail() != null) {
            existingUser.setEmail(userView.getEmail());
        }
        if (userView.getName() != null) {
            existingUser.setName(userView.getName());
        }
        if (userView.getPassword() != null) {
            existingUser.setPassword(userView.getPassword());
        }
        if (userView.getProfilePicture() != null) {
            existingUser.setProfilePicture(userView.getProfilePicture());
        }

        // Salva o usuário atualizado
        User updatedUser = userRepository.save(existingUser);
    
        return modelMapper.map(updatedUser, UserBaseView.class);
    }

    @Override
    public UserBaseView read(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
        return modelMapper.map(user, UserBaseView.class);
    }

    @Override
    public UserBaseView delete(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));

        // Mapeia o usuário para UserView, exclui o usuário e retorna UserView
        UserBaseView userView = modelMapper.map(user, UserBaseView.class);
        userRepository.delete(user);
        return userView;
    }

    @Override
    public List<ProjectBaseView> findProjects(UUID id) {
        modelMapper = new ModelMapper();
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
        
        return relationUserProjectRepository.findProjectByUserId(user.getId())
            .map(projects -> projects.stream()
                .map(project -> modelMapper.map(project, ProjectBaseView.class))
                .collect(Collectors.toList()))
            .orElseThrow(() -> new BusinessException("Nenhum projeto associado a este usuário"));
    }

    private void validateUserView(UserView userView) {
        // Validação simples dos parâmetros do UserView
        if (userView.getName() == null || userView.getEmail() == null || userView.getPassword() == null) {
            throw new BusinessException(INVALID_PARAMETERS_MESSAGE);
        }
    }

}
