INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('42','0', '20', '1', '0', 'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('42',0, '<p>You should include only supporting information in the appendix. You shouldn’t use it to provide your responses to the question.</p><p>Guidance for this section needs to be created</p>', 'How do I create my Je-S output?', 0, 1, 'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'', 0, 0, '20', '1', '7');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('42', '42', '0');