// Define the start node ID
WITH "d1914989-2c92-4bc4-8cf7-eb59af586104" AS startNodeId

// Match all paths up to 1 hop from the start node
MATCH path = (start:person)-[*0..2]-(connected)
WHERE start.id = startNodeId AND (start <> connected OR start = connected)

// Collect all unique nodes and relationships
WITH 
    COLLECT(DISTINCT start) + COLLECT(DISTINCT connected) AS allNodes,
    COLLECT(DISTINCT relationships(path)) AS allRels

// Unwind the collections to create a graph structure
UNWIND allNodes AS node
UNWIND allRels AS rels
UNWIND rels AS rel

// Return the graph structure as a D3.js-friendly JSON object
RETURN {
    nodes: COLLECT(DISTINCT {
        id: node.id,
        label: labels(node)[0],
        name: COALESCE(node.name, "N/A"),
        group: labels(node)[0]
    }),
    links: COLLECT(DISTINCT {
        source: startNode(rel).id,
        target: endNode(rel).id,
        type: type(rel)
    })
} AS d3Graph