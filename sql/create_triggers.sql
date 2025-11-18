-- Health Track Personal Wellness Platform
-- Triggers for Business Rule Enforcement
-- Group 27

USE healthtrack;

-- Drop existing triggers if they exist
DROP TRIGGER IF EXISTS trg_appointment_cancel_validation;
DROP TRIGGER IF EXISTS trg_invitation_expires_at;
DROP TRIGGER IF EXISTS trg_family_group_min_members;
DROP TRIGGER IF EXISTS trg_family_group_min_members_update;
DROP TRIGGER IF EXISTS trg_action_type_consistency_insert;
DROP TRIGGER IF EXISTS trg_action_type_consistency_update;
DROP TRIGGER IF EXISTS trg_challenge_type_consistency_insert;
DROP TRIGGER IF EXISTS trg_challenge_type_consistency_update;
DROP TRIGGER IF EXISTS trg_action_child_required;
DROP TRIGGER IF EXISTS trg_challenge_participant_min_check;
DROP TRIGGER IF EXISTS trg_email_normalize;
DROP TRIGGER IF EXISTS trg_email_normalize_update;
DROP TRIGGER IF EXISTS trg_phone_normalize;
DROP TRIGGER IF EXISTS trg_phone_normalize_update;
DROP TRIGGER IF EXISTS trg_primary_provider_unique_insert;
DROP TRIGGER IF EXISTS trg_primary_provider_unique_update;

-- 1. Appointment Cancellation Validation Trigger
-- Ensures cancellation is at least 24 hours before scheduled time
DELIMITER $$

CREATE TRIGGER trg_appointment_cancel_validation
BEFORE UPDATE ON APPOINTMENT
FOR EACH ROW
BEGIN
    IF NEW.status = 'Cancelled' AND OLD.status != 'Cancelled' THEN
        -- Check if cancel_time is at least 24 hours before scheduled_at
        IF NEW.cancel_time IS NULL OR NEW.cancel_time > DATE_SUB(NEW.scheduled_at, INTERVAL 24 HOUR) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Appointment can only be cancelled at least 24 hours before scheduled time';
        END IF;
        
        -- Ensure cancel_reason is provided
        IF NEW.cancel_reason IS NULL OR TRIM(NEW.cancel_reason) = '' THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cancel reason is required when cancelling an appointment';
        END IF;
        
        -- Set cancel_time if not provided
        IF NEW.cancel_time IS NULL THEN
            SET NEW.cancel_time = NOW();
        END IF;
    END IF;
END$$

-- 2. Invitation Expiration Trigger
-- Sets expires_at to 15 days after initiated_at if not provided
CREATE TRIGGER trg_invitation_expires_at
BEFORE INSERT ON INVITATION
FOR EACH ROW
BEGIN
    IF NEW.expires_at IS NULL THEN
        SET NEW.expires_at = DATE_ADD(NEW.initiated_at, INTERVAL 15 DAY);
    END IF;
END$$

-- 3. Family Group Minimum Members Trigger
-- Ensures each family group has at least 2 active members
CREATE TRIGGER trg_family_group_min_members
AFTER DELETE ON GROUP_MEMBER
FOR EACH ROW
BEGIN
    DECLARE active_count INT;
    
    -- Count active members (where left_at IS NULL)
    SELECT COUNT(*) INTO active_count
    FROM GROUP_MEMBER
    WHERE group_id = OLD.group_id AND left_at IS NULL;
    
    -- If less than 2 active members, prevent deletion
    IF active_count < 2 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Family group must have at least 2 active members';
    END IF;
END$$

-- Also check on UPDATE when setting left_at
CREATE TRIGGER trg_family_group_min_members_update
BEFORE UPDATE ON GROUP_MEMBER
FOR EACH ROW
BEGIN
    DECLARE active_count INT;
    
    -- If updating left_at to a non-null value (member leaving)
    IF OLD.left_at IS NULL AND NEW.left_at IS NOT NULL THEN
        -- Count active members excluding the one being updated
        SELECT COUNT(*) INTO active_count
        FROM GROUP_MEMBER
        WHERE group_id = OLD.group_id 
          AND group_member_id != OLD.group_member_id
          AND left_at IS NULL;
        
        -- If less than 1 active member will remain, prevent update
        IF active_count < 1 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Family group must have at least 2 active members';
        END IF;
    END IF;
END$$

-- 4. Action Type Consistency Triggers (Total Disjoint Specialization)
-- Ensure ACTION.action_type matches the child table

-- Trigger for APPOINTMENT insert
CREATE TRIGGER trg_action_type_consistency_insert
BEFORE INSERT ON APPOINTMENT
FOR EACH ROW
BEGIN
    DECLARE action_type_val VARCHAR(20);
    
    SELECT action_type INTO action_type_val
    FROM ACTION
    WHERE action_id = NEW.action_id;
    
    IF action_type_val != 'Appointment' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Action type must be Appointment for APPOINTMENT table';
    END IF;
END$$

-- Trigger for APPOINTMENT update
CREATE TRIGGER trg_action_type_consistency_update
BEFORE UPDATE ON APPOINTMENT
FOR EACH ROW
BEGIN
    DECLARE action_type_val VARCHAR(20);
    
    SELECT action_type INTO action_type_val
    FROM ACTION
    WHERE action_id = NEW.action_id;
    
    IF action_type_val != 'Appointment' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Action type must be Appointment for APPOINTMENT table';
    END IF;
END$$

-- Trigger for CHALLENGE insert
CREATE TRIGGER trg_challenge_type_consistency_insert
BEFORE INSERT ON CHALLENGE
FOR EACH ROW
BEGIN
    DECLARE action_type_val VARCHAR(20);
    
    SELECT action_type INTO action_type_val
    FROM ACTION
    WHERE action_id = NEW.action_id;
    
    IF action_type_val != 'Challenge' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Action type must be Challenge for CHALLENGE table';
    END IF;
END$$

-- Trigger for CHALLENGE update
CREATE TRIGGER trg_challenge_type_consistency_update
BEFORE UPDATE ON CHALLENGE
FOR EACH ROW
BEGIN
    DECLARE action_type_val VARCHAR(20);
    
    SELECT action_type INTO action_type_val
    FROM ACTION
    WHERE action_id = NEW.action_id;
    
    IF action_type_val != 'Challenge' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Action type must be Challenge for CHALLENGE table';
    END IF;
END$$

-- Trigger to ensure ACTION has corresponding child record
CREATE TRIGGER trg_action_child_required
AFTER INSERT ON ACTION
FOR EACH ROW
BEGIN
    IF NEW.action_type = 'Appointment' THEN
        -- Check if APPOINTMENT record exists (will be inserted after ACTION)
        -- This is a soft check, actual enforcement happens in application layer
        SET @action_waiting_for_child = NEW.action_id;
    ELSEIF NEW.action_type = 'Challenge' THEN
        SET @action_waiting_for_child = NEW.action_id;
    END IF;
END$$

-- 5. Challenge Participant Minimum Check
-- Ensure each challenge has at least 1 participant with status 'Joined' or 'Invited'
CREATE TRIGGER trg_challenge_participant_min_check
AFTER DELETE ON CHALLENGE_PARTICIPANT
FOR EACH ROW
BEGIN
    DECLARE participant_count INT;
    
    SELECT COUNT(*) INTO participant_count
    FROM CHALLENGE_PARTICIPANT
    WHERE action_id = OLD.action_id
      AND participant_status IN ('Joined', 'Invited');
    
    IF participant_count = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Challenge must have at least one participant with status Joined or Invited';
    END IF;
END$$

-- 6. Email Normalization Trigger (lowercase)
CREATE TRIGGER trg_email_normalize
BEFORE INSERT ON EMAIL
FOR EACH ROW
BEGIN
    SET NEW.email = LOWER(TRIM(NEW.email));
END$$

CREATE TRIGGER trg_email_normalize_update
BEFORE UPDATE ON EMAIL
FOR EACH ROW
BEGIN
    SET NEW.email = LOWER(TRIM(NEW.email));
END$$

-- 7. Phone Normalization Trigger (remove spaces, dashes, parentheses)
CREATE TRIGGER trg_phone_normalize
BEFORE INSERT ON PHONE
FOR EACH ROW
BEGIN
    SET NEW.phone_number = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        TRIM(NEW.phone_number), ' ', ''), '-', ''), '(', ''), ')', ''), '.', '');
END$$

CREATE TRIGGER trg_phone_normalize_update
BEFORE UPDATE ON PHONE
FOR EACH ROW
BEGIN
    SET NEW.phone_number = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        TRIM(NEW.phone_number), ' ', ''), '-', ''), '(', ''), ')', ''), '.', '');
END$$

-- 8. Primary Provider Unique Constraint Triggers
-- Ensures each user has at most one primary provider (is_primary = TRUE)
CREATE TRIGGER trg_primary_provider_unique_insert
BEFORE INSERT ON USER_PROVIDER
FOR EACH ROW
BEGIN
    DECLARE primary_count INT;
    
    IF NEW.is_primary = TRUE THEN
        SELECT COUNT(*) INTO primary_count
        FROM USER_PROVIDER
        WHERE user_id = NEW.user_id 
          AND is_primary = TRUE 
          AND link_status = 'Active';
        
        IF primary_count > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Each user can have at most one primary provider';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_primary_provider_unique_update
BEFORE UPDATE ON USER_PROVIDER
FOR EACH ROW
BEGIN
    DECLARE primary_count INT;
    
    -- If setting is_primary to TRUE
    IF NEW.is_primary = TRUE AND (OLD.is_primary = FALSE OR OLD.is_primary IS NULL) THEN
        SELECT COUNT(*) INTO primary_count
        FROM USER_PROVIDER
        WHERE user_id = NEW.user_id 
          AND is_primary = TRUE 
          AND link_status = 'Active'
          AND user_provider_id != NEW.user_provider_id;
        
        IF primary_count > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Each user can have at most one primary provider';
        END IF;
    END IF;
END$$

DELIMITER ;

