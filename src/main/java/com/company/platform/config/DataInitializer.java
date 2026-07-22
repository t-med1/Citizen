package com.company.platform.config;

import com.company.platform.entity.*;
import com.company.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintRepository complaintRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role admin = ensureRole("ROLE_ADMIN", "Administrateur de la plateforme");
        Role agent = ensureRole("ROLE_AGENT", "Agent traitant les réclamations");
        Role citizen = ensureRole("ROLE_CITIZEN", "Citoyen déposant des réclamations");

        User adminUser = ensureUser("Admin Principal", "admin@platform.com", "Admin@123", "0600000001", admin);
        User agentUser = ensureUser("Agent Municipal", "agent@platform.com", "Agent@123", "0600000002", agent);
        User citizenUser = ensureUser("Citoyen Test", "citizen@platform.com", "Citizen@123", "0600000003", citizen);

        Category voirie = ensureCategory("Voirie", "Nids-de-poule, chaussées endommagées, signalisation");
        Category eclairage = ensureCategory("Éclairage public", "Lampadaires en panne ou défectueux");
        Category proprete = ensureCategory("Propreté", "Collecte des déchets, dépôts sauvages");
        Category espacesVerts = ensureCategory("Espaces verts", "Parcs, jardins, arbres");
        Category securite = ensureCategory("Sécurité", "Incidents de sécurité sur la voie publique");
        ensureCategory("Autre", "Toute autre demande de service");

        if (complaintRepository.count() == 0) {
            Complaint c1 = complaintRepository.save(Complaint.builder()
                    .title("Nid-de-poule dangereux Avenue Hassan II")
                    .description("Un grand nid-de-poule s'est formé près du carrefour, risque pour les deux-roues.")
                    .location("Avenue Hassan II, Casablanca")
                    .status(ComplaintStatus.IN_PROGRESS)
                    .citizen(citizenUser)
                    .assignedAgent(agentUser)
                    .category(voirie)
                    .build());

            complaintRepository.save(Complaint.builder()
                    .title("Lampadaire éteint depuis une semaine")
                    .description("Le lampadaire au coin de la rue ne fonctionne plus, la rue est très sombre la nuit.")
                    .location("Rue des Fleurs, Casablanca")
                    .status(ComplaintStatus.NEW)
                    .citizen(citizenUser)
                    .category(eclairage)
                    .build());

            complaintRepository.save(Complaint.builder()
                    .title("Dépôt sauvage d'ordures")
                    .description("Des sacs poubelle s'accumulent depuis plusieurs jours près du marché.")
                    .location("Marché central, Casablanca")
                    .status(ComplaintStatus.RESOLVED)
                    .citizen(citizenUser)
                    .assignedAgent(agentUser)
                    .category(proprete)
                    .build());

            complaintRepository.save(Complaint.builder()
                    .title("Arbre menaçant de tomber")
                    .description("Un arbre du parc penche dangereusement après les dernières pluies.")
                    .location("Parc de la Ligue Arabe, Casablanca")
                    .status(ComplaintStatus.WAITING)
                    .citizen(citizenUser)
                    .assignedAgent(agentUser)
                    .category(espacesVerts)
                    .build());

            commentRepository.save(Comment.builder()
                    .content("Équipe technique envoyée sur place, intervention prévue sous 48h.")
                    .complaint(c1)
                    .author(agentUser)
                    .build());
        }
    }

    private Role ensureRole(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).description(description).build()));
    }

    private Category ensureCategory(String name, String description) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(name).description(description).build()));
    }

    private User ensureUser(String fullName, String email, String rawPassword, String phone, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            User user = User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .phone(phone)
                    .enabled(true)
                    .roles(roles)
                    .build();
            return userRepository.save(user);
        });
    }
}
