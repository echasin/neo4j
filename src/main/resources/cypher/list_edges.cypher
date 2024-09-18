MATCH (start)-[r]->(end)
RETURN 
    TYPE(r) AS RelationshipType,
    startNode(r).name AS FromNode,
    endNode(r).name AS ToNode,
    properties(r) AS RelationshipProperties
LIMIT 100