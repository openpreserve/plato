DROP PROCEDURE IF EXISTS calculateRootNode;

DELIMITER //

CREATE PROCEDURE calculateRootNode(IN tnId INTEGER, OUT rootId INTEGER)
BEGIN
	DECLARE parent INTEGER;
	DECLARE parentRoot INTEGER;
	
	SELECT parent_fk INTO parent FROM TreeNode WHERE id=tnId;
	
	IF parent IS NULL THEN
		SET rootId = tnId;
	ELSE 
		CALL calculateRootNode(parent, parentRoot);
		SET rootId = parentRoot;
	END IF;
END //

DELIMITER ;


DROP FUNCTION IF EXISTS rootNode;

DELIMITER //

CREATE FUNCTION rootNode(tnId INTEGER)
RETURNS INTEGER
BEGIN
	DECLARE rootId INTEGER;
	
	CALL calculateRootNode(tnId, rootId);
	
	RETURN rootId;
END //

DELIMITER ;


DROP PROCEDURE IF EXISTS calculateRelativeWeight;

DELIMITER //

CREATE PROCEDURE calculateRelativeWeight(IN tnId INTEGER, OUT relWeight DOUBLE)
BEGIN
	DECLARE wght DOUBLE;
	DECLARE parentRelWeight DOUBLE;
	DECLARE parent INTEGER;
	
	SELECT weight, parent_fk INTO wght, parent FROM TreeNode WHERE id=tnId;
	
	IF parent IS NULL THEN
		SET relWeight = wght;
	ELSE 
		CALL calculateRelativeWeight(parent, parentRelWeight);
		SET relWeight = wght * parentRelWeight;
	END IF;
END //

DELIMITER ;


DROP FUNCTION IF EXISTS relativeWeight;

DELIMITER //

CREATE FUNCTION relativeWeight(tnId INTEGER)
RETURNS DOUBLE
BEGIN
	DECLARE relWeight DOUBLE;
	
	CALL calculateRelativeWeight(tnId, relWeight);
	
	RETURN relWeight;
END //

DELIMITER ;


DROP TABLE IF EXISTS VPlanLeaf_ValueList;

DROP TABLE IF EXISTS VPlanLeaf;

CREATE OR REPLACE VIEW VPlanLeaf AS SELECT t.id, weight AS absoluteWeight, relativeWeight(t.id) AS relativeWeight, t.scale_id, t.transformer_id, t.criterion_id, p.id as planId, t.aggregationMode FROM TreeNode t, ObjectiveTree o, Plan p WHERE t.nodetype='L' AND rootNode(t.id) IN (SELECT o.root_id FROM Plan p, PlanProperties pp, ObjectiveTree o WHERE (pp.state="WEIGHTS_SET" OR pp.state="ANALYSED" OR pp.state="EXECUTEABLE_PLAN_CREATED" OR pp.state="PLAN_DEFINED" OR pp.state="PLAN_VALIDATED") AND pp.name NOT LIKE 'MY DEMO PLAN%' AND p.planProperties_id=pp.id AND p.tree_id=o.id) AND rootNode(t.id)=o.root_id AND o.id=p.tree_id;

CREATE OR REPLACE VIEW VPlanLeaf_ValueList AS SELECT TreeNode_id as VPlanLeaf_id, valueMap_id, valueMap_KEY FROM TreeNode_ValueList WHERE (TreeNode_id, valueMap_KEY) IN (SELECT l.id, a.name FROM VPlanLeaf l, Plan p, AlternativesDefinition ad, Alternative a WHERE l.planId=p.id AND ad.id=p.alternativesdefinition_id AND a.parent_id=ad.id AND a.discarded=0);
