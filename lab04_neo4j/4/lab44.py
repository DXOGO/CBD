from os import write
import csv
from neo4j import GraphDatabase

# pip3 install neo4j

# more here

class lab44c:
    
    def __init__(self, uri, user, password):
        self.driver = GraphDatabase.driver(uri, auth=(user, password))
        
        self.questions = ["1. List all different job titles",
                              "2. List all jobs that have an anual salary of over $100K",
                              "3. List 5 highest paying salaries in the company Pfizer",
                              "4. List industries that belong to the 'Media' sector",
                              "5. List information from the oldest company, including industry and sector",
                              "6. List companies in the 'Biotech' sector that start their name with an 'A",
                              "7. List all different jobs in the 'Finance' Sector",
                              "8. List all job positions in the company 'Autodesk 4.0' and salary",
                              "9. List number of job positions there are in the company 'Autodesk 4.0' and the average salary",
                              "10. List top 3 jobs with the best rating and their companies, ordered by rating descending"]
        
        self.answers = ["MATCH (j:Job) RETURN DISTINCT(j.job_title) AS Job",
                        "MATCH (j:Job)-[r:WORKS_IN]->(c:Company) with c,j where j.salary > 100000 RETURN c.name as Company, j.job_title as Job, j.salary as AnualSalary",
                        "MATCH (j:Job)-[r:WORKS_IN]->(c:Company {name : 'Pfizer 4.0'}) with j,c order by j.salary desc limit 5 RETURN j.job_title as Job, j.salary as AnualSalary",
                        "MATCH (s:Sector)<-[r:BELONGS_TO]-(i:Industry) WHERE s.name = 'Media' RETURN i.type AS Industry",
                        "MATCH (c:Company)-[:SPECIALIZES]->(i:Industry)-[:BELONGS_TO]->(s:Sector) WITH i, s, c, c.founded as age ORDER BY age asc LIMIT 1 RETURN c.name as Company, age as Foundation, i.type as Industry, s.name as Sector",
                        "MATCH (c:Company)-[:SPECIALIZES]->(i:Industry)-[:BELONGS_TO]->(s:Sector) WHERE c.name STARTS WITH 'A' AND s.name CONTAINS 'Biotech' RETURN c.name as Company, s.name as Sector",
                        "MATCH (j:Job)-[:WORKS_IN]->(c:Company)-[:SPECIALIZES]->(i:Industry)-[:BELONGS_TO]->(s:Sector) WHERE s.name = 'Finance' RETURN j.job_title as Job, j.salary as AnualSalary, c.name as Company",
                        "MATCH (j:Job)-[:WORKS_IN]->(c:Company {name : 'Autodesk 4.0'}) RETURN j.job_title as Job, j.salary as AnualSalary",
                        "MATCH (j:Job)-[:WORKS_IN]->(c:Company {name : 'Autodesk 4.0'}) WITH count(j.job_title) AS JobCount, avg(j.salary) AS AverageSalary  RETURN JobCount, AverageSalary",
                        "MATCH (j:Job)-[:WORKS_IN]->(c:Company) WITH j, c ORDER BY j.rating DESC LIMIT 3 RETURN j.job_title as Job, j.rating as Rating, c.name as Company"]
    
    def create(self):
        self.driver.session().run(
            "LOAD CSV WITH HEADERS FROM 'file:///data_cleaned_2022.csv' AS row  \
            MERGE (j:Job {id: row.index, job_title: row.job_title, salary: toInteger(row.salary), rating : toFloat(row.rating)}) \
            MERGE (c:Company {name: row.company_name, founded : toInteger(row.founded)}) \
            MERGE (i:Industry {type : row.industry_type}) \
            MERGE (s:Sector {name : row.sector_name}) \
            \
            MERGE (j)-[r1:WORKS_IN]->(c) \
            MERGE (c)-[r2:SPECIALIZES]->(i) \
            MERGE (i)-[r3:BELONGS_TO]->(s);");

    def queriesAndSave(self):
        f = open("CBD_L44c_output.txt", "a")
        for i, query in enumerate(self.answers):
            result = self.driver.session().run(query)
            f.write("\n\n" + self.questions[i] + "\n")
            f.write(query + "\n\n---")

            results=[r for r in result.data()]

            for k in results[0].keys():
                f.write(f" {k}                  ")
            
            f.write("---")
            
            for r in results[:10]:
                f.write("\n")
                for v in r.values():
                    f.write(f" {str(v)}             ")
                    
            if(len(results)>10):
                f.write("\n...")

            f.write("\n")
        f.close()
            

    def close(self):
        self.driver.close()


if __name__ == "__main__":
    try:
        bd = lab44c("bolt://localhost:7687", "neo4j", "password")
        print("Connected to database!")
        bd.create()
        bd.queriesAndSave()
        print("All queries were sucessfully executed. Check the CBD_L44c_output.txt for the results!")
        bd.close()
    except Exception as e:
        print("There was an error!", e)
        print("Maybe check if you have neo4j installed for Python, if not run 'pip3 install neo4j' in any terminal!")