package com.gestiontests.service;

import com.gestiontests.entity.*;
import com.gestiontests.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class QuestionService {
    
    @Inject
    private QuestionRepository questionRepository;
    
    @Inject
    private ReponsePossibleRepository reponsePossibleRepository;
    
    @Inject
    private ThemeRepository themeRepository;
    
    @Inject
    private TypeQuestionRepository typeQuestionRepository;
    
    @Transactional
    public Question createQuestion(Map<String, Object> questionData) throws Exception {
        // Récupérer le thème
        Integer themeId = (Integer) questionData.get("themeId");
        Optional<Theme> themeOpt = themeRepository.findById(themeId);
        if (themeOpt.isEmpty()) {
            throw new Exception("Thème non trouvé");
        }
        
        // Récupérer le type de question
        Integer typeId = (Integer) questionData.get("typeQuestionId");
        Optional<TypeQuestion> typeOpt = typeQuestionRepository.findById(typeId);
        if (typeOpt.isEmpty()) {
            throw new Exception("Type de question non trouvé");
        }
        
        // Créer la question
        Question question = new Question();
        question.setTheme(themeOpt.get());
        question.setTypeQuestion(typeOpt.get());
        question.setLibelle((String) questionData.get("libelle"));
        question.setExplication((String) questionData.get("explication"));
        
        Question savedQuestion = questionRepository.create(question);
        
        // Ajouter les réponses possibles si fournies
        if (questionData.containsKey("reponses")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> reponsesData = (List<Map<String, Object>>) questionData.get("reponses");
            
            for (Map<String, Object> reponseData : reponsesData) {
                ReponsePossible reponse = new ReponsePossible();
                reponse.setQuestion(savedQuestion);
                reponse.setLibelle((String) reponseData.get("libelle"));
                reponse.setEstCorrect((Boolean) reponseData.get("estCorrect"));
                
                reponsePossibleRepository.create(reponse);
            }
        }
        
        return savedQuestion;
    }
    
    @Transactional
    public Question updateQuestion(Integer id, Map<String, Object> questionData) throws Exception {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            throw new Exception("Question non trouvée");
        }
        
        Question question = questionOpt.get();
        
        // Mettre à jour les champs de base
        if (questionData.containsKey("themeId")) {
            Integer themeId = (Integer) questionData.get("themeId");
            Optional<Theme> themeOpt = themeRepository.findById(themeId);
            if (themeOpt.isEmpty()) {
                throw new Exception("Thème non trouvé");
            }
            question.setTheme(themeOpt.get());
        }
        
        if (questionData.containsKey("typeQuestionId")) {
            Integer typeId = (Integer) questionData.get("typeQuestionId");
            Optional<TypeQuestion> typeOpt = typeQuestionRepository.findById(typeId);
            if (typeOpt.isEmpty()) {
                throw new Exception("Type de question non trouvé");
            }
            question.setTypeQuestion(typeOpt.get());
        }
        
        if (questionData.containsKey("libelle")) {
            question.setLibelle((String) questionData.get("libelle"));
        }
        
        if (questionData.containsKey("explication")) {
            question.setExplication((String) questionData.get("explication"));
        }
        
        return questionRepository.update(question);
    }
    
    @Transactional
    public void deleteQuestion(Integer id) throws Exception {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            throw new Exception("Question non trouvée");
        }
        
        questionRepository.deleteById(id);
    }
    
    public List<Question> findAll() {
        return questionRepository.findAll();
    }
    
    public List<Question> findByTheme(Integer themeId) {
        return questionRepository.findByTheme(themeId);
    }
    
    public List<Theme> findAllThemes() {
        return themeRepository.findAll();
    }
    
    public List<TypeQuestion> findAllTypesQuestions() {
        return typeQuestionRepository.findAll();
    }
    
    public long count() {
        return questionRepository.count();
    }
    
    public Map<String, Object> getStatsQuestions() {
        long total = questionRepository.count();
        List<Theme> themes = themeRepository.findAll();
        
        long questionsSansReponses = questionRepository.findQuestionsWithoutReponses().size();
        long questionsSansReponsesCorrectes = questionRepository.findQuestionsWithoutCorrectReponses().size();
        
        return Map.of(
            "total", total,
            "parTheme", themes.stream().collect(
                java.util.stream.Collectors.toMap(
                    Theme::getNom,
                    theme -> questionRepository.countByTheme(theme.getId())
                )
            ),
            "questionsSansReponses", questionsSansReponses,
            "questionsSansReponsesCorrectes", questionsSansReponsesCorrectes,
            "tauxCompletude", total > 0 ? (double) (total - questionsSansReponses) / total * 100 : 0
        );
    }
    
    @Transactional
    public List<Question> findAllWithDetails() {
        return questionRepository.findAllWithReponses();
    }
}
