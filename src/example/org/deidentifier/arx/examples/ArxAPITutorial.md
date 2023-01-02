# ARX API Tutorial:

## Notes:

- Some functionalities are only available via GUI not API e.g. reading or creating projects.
  - Suggestion: Keep the GUI separated and move all non related fucntionalities to the main. 

## Terminologies:
 - Critiron, privacy model: same meaning. The output anonymized data must meet the defined privacy model. 
 - Transformation, Transformed data, output data: The transformation transforms the input data using e.g. generalization hierarchies, suppression, and agreggation to produce the transformed anonymized data. The output anonymized data is the selected anonymized transformed data. 
 - Metric, quality model: same meaning. This the metric that decides the best transformation. 
 - Solution space: All available transformations e.g. represented by the lattice tree.
 - Algorithm: this is the algorithm that search the solution space and find the best transformation e.g. lightning, flash or genitic.
 - Framework: is the Arx pipline.  
 - Risk: risk package has different methods to evaluate the risk of the anonymized output data.
 - Arx certificate: A short report about the anonymization process and the result in pdf format. 

## Folder Structure in Arx packages (TODO):
 - [arx](): contain the main classes e.g. Anonymizer, Configueration, Lattice, Result, Classification and Solver configuerations, ProcessStatistics, all data related classes, and Attribute class. 
   - Suggestion: Move all data related classes to an external repository that provides useful data I/O and processing functions. 
 - [arx.aggregate](): All classes related to aggregation, classification hierarchies, and statistics.
 - [arx.aggregate.classification](): All supported classiciation methods. Currently Linear regression, Naive Basian, and Random forests. More about this can be found [here]().
 - [arx.aggregate.quality](): All classes related to data quality.
 - arx.algorithm: All classes related to solution space search algorithms. Currently: Flash (optimal), Lightning, Genetic, and ed-DP. 
 - arx.certificate: All classes related to generating Arx certificate.
 - arx.certificate.elements
 - arx.certificate.resources
 - arx.common: Classes for mapping, wrapping, and grouping.
 - arx.criteria: privacy model classes. Note K-anonymity is implemented in the framework. 
 - arx.dp: classes related to Differntial Privacy. 
 - arx.exceptions: All Arx customized exceptions. 
 - arx.framwork: 
 - arx.framwork.check
 - arx.framwork.distribution
 - arx.framwork.groupify
 - arx.framwork.history
 - arx.framwork.transformer
 - arx.framwork.data: Why we have a nother Data class?
 - arx.framwork.lattice: It uses an external lattice library to generate the solution space. 
 - arx.io: All input output classes e.g. from csv, excel or dtabases. 
 - arx.metric: All classes related to data quality. 
 - arx.metric.v2
 - arx.reliablity
 - arx.risk: All classes related to risk models.
 - arx.risk.resources.us

**Tips for eclipse (TODO):**
 - Open and build Arx from source code.
 - Use Arx as library. 
 - Using formatter.
 - Tracing abstract function.
 - Show function calls hierarcy
 - Navigating backward/forward
 - Show file in project tree.
 - Reset windows.
 - Debugging.
 - Class outlines. 
 - Java doc for current file
 - Check problems
 - Commit changes to a new branch.


 ## API Howtos:
 ### Data
- [Create a data and hierarchies manually](#create-a-data-and-hierarchies-manually)
- [Import or read data and hierarchies from csv files](#import-or-read-data-and-hierarchies-from-csv-files)
- [Use data handles for input and output data](#)
- [Change Attribute data type e.g. to date](#change-attribute-data-type-eg-to-date)
- [Change Attribute data type format e.g. to dd.MM.yyyy](#change-attribute-data-type-format-eg-to-ddmmyyyy)
- [Change Attribute type e.g. to sensitive or quasi-identifier](#change-attribute-type-eg-to-sensitive-or-quasi-identifier)
- [Use attribute without hierarchy](#change-attribute-type-eg-to-sensitive-or-quasi-identifier)

- [Export or write data and hierarchies to csv files](#export-or-write-data-and-hierarchies-to-csv-files)
### Configueration
- [Create anonymization configueration](#create-anonymization-configueration)
- [Change the supression limit](#create-anonymization-configueration)
- [Change the PracticalMonotonicity](#create-anonymization-configueration)
- [Change the Maximum Outliers](#create-anonymization-configueration)
#### Algorithm
- [Change anonymization configueration algorithm ](#)
- [Use Heuristic Search algorithm ](#)
- [Use Genetic algorithm](#)
#### Privacy models
- [Add K-anonymity privacy model (critirion)](#)
- [Add l-diversity privacy model (critirion)](#)
- [Add t-closeness privacy model (critirion)](#)
#### Data Quality 
- [Change anonymization configueration data quality (metric)](#)
- [Use quality model (metric) HeightMetric](#)
- [Use quality model (metric) Loss with weights](#)
### Anonymization
- [Start anonymization process](#start-anonymization-process)
### Research Subset
- [Create subset research data manually](#create-subset-research-data-manually)
- [Create subset research data using simple selector (query)](#create-subset-research-data-using-simple-selector-query)
- [Create subset research data using simple selector (query)](#create-subset-research-data-using-comblix-selector-query)
### Exploration
- [Find specific transformation](#)
- [Apply a transformation](#)
### Printing
- [Print input data, output data, input research subset, and output research subset](#print-input-and-output-data)
- [Print anonymization process info](#)
- [Print input and result statistics](#)
- [Use classification to evaluate the result](#)
- [Print risk information Example29](#)
### TODOs
- [Example 17 how to list the available data types](#)
- [Example 18 - how to use the builders for generalization hierarchies](#)
- [Example 19 - how to use the API for creating different output representations of an input dataset](#)
- [Example 20 - how to use aggregate functions](#)
- [Example 21 - demonstrates the use of the data import facilities provided by the ARX framework. Data can be imported from various types of sources, e.g. CSV files, Excel files and databases (using JDBC). The API is mostly the same for all of these sources, although not all options might be available in each case. Refer to the comments further down below for details about particular sources.](#)
- [Example 22 - how to use the l-diversity privacy model without protecting sensitive assocations](#)
- [Example 23 - how to use multiple instances of l-diversity without protecting sensitive associations](#)
- [Example 24 - how to directly use empty and functional hierarchies](#)
- [Example 25 - an example for using the generalized loss metric with different types of generalization hierarchies](#)
- [Example 26 - how to use an interval-based hierarchy builder with high precision](#)
- [Example 27 - how to use data cleansing capabilities](#)
- [Example 28 - how to use data cleansing using the DataSource functionality](#)
- [Example 29 - how to perform risk analyses with the API](#)
- [Example 30 - how to compute summary statistics](#)
- [Example 31 - how to use microaggregation](#)
- [Example 32 - how to use microaggregation with generalization](#)
- [Example 33 - how to use microaggregation. It also demonstrates how, since - version 3.1. of ARX attribute types and transformation methods should be specified separately](#)
- [Example 34 - how to use a heuristic search algorithm](#)
- [Example 35 - how to find HIPAA identifiers](#)
- [Example 36 - how to use utility-based microaggregation](#)
- [Example 37 - how to use data-dependent and data-independent (e,d)-DP](#)
- [Example 38 - how to use local recoding with ARX](#)
- [Example 39 - how to compare data mining performance](#)
- [Example 40 - how to compare data mining performance](#)
- [Example 41 - how to use the k-map model](#)
- [Example 42 - how to use the k-map and d-presence models combined](#)
- [Example 43 - how to evaluate combined risk metrics](#)
- [Example 44 - how to use the k-map privacy model with a statistical estimator](#)
- [Example 45 - how to use the mixed risk model](#)
- [Example 46 - how to use the distribution of risks](#)
- [Example 47 - an example for evaluating distinction and separation of attributes as described in R. Motwani et al. "Efficient algorithms for masking and finding quasi-identifiers" Proc. VLDB Conf., 2007.](#)
- [Example 48 - how to use ordered distance t-closeness. Implements Example 3 from the paper Li et al. "t-Closeness: Privacy Beyond k-Anonymity and l-Diversity"](#)
- [Example 49 - examples of using the no-attack variant of the game-theoretic approach for performing a monetary cost/benefit analysis using prosecutor risk](#)
- [Example 50 - examples of using the no-attack variant of the game-theoretic approach for performing a monetary cost/benefit analysis using journalist risk](#)
- [Example 51 - examples of using the game-theoretic approach for performing a monetary cost/benefit analysis using prosecutor risk](#)
- [Example 52 - examples of using the game-theoretic approach for performing a monetary cost/benefit analysis using journalist risk](#)
- [Example 53 - how to generate reports](#)
- [Example 54 - how to access quality statistics](#)
- [Example 55 - how to use a fast algorithm for local recoding with ARX](#)
- [Example 56 - how to evaluate risk with wildcard matching](#)
- [Example 57 - how to analyze risks with wildcards for data transformed with cell suppression](#)
- [Example 58 - an example that shows consistent handling of suppressed records in input and output](#)
- [Example 59 - an example that shows handling of suppressed values and records in input data](#)
- [Example 60 - an example of processing high-dimensional data](#)


### Create a data and hierarchies manually

        import org.deidentifier.arx.Data.DefaultData;
        import org.deidentifier.arx.AttributeType.Hierarchy;
        import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;

        DefaultData data = Data.create();
        data.add("age", "gender", "zipcode");
        data.add("34", "male", "81667");
        data.add("45", "female", "81675");
        data.add("66", "male", "81925");
        data.add("70", "female", "81931");
        data.add("34", "female", "81931");
        data.add("70", "male", "81931");
        data.add("45", "male", "81931");

        // Define hierarchies
        DefaultHierarchy ageHierarchy = Hierarchy.create();
        ageHierarchy.add("34", "<50", "*");
        ageHierarchy.add("45", "<50", "*");
        ageHierarchy.add("66", ">=50", "*");
        ageHierarchy.add("70", ">=50", "*");

        DefaultHierarchy genderHierarchy = Hierarchy.create();
        genderHierarchy.add("male", "*");
        genderHierarchy.add("female", "*");

        DefaultHierarchy zipcodeHierarchy = Hierarchy.create();
        zipcodeHierarchy.add("81667", "8166*", "816**", "81***", "8****", "*****");
        zipcodeHierarchy.add("81675", "8167*", "816**", "81***", "8****", "*****");
        zipcodeHierarchy.add("81925", "8192*", "819**", "81***", "8****", "*****");
        zipcodeHierarchy.add("81931", "8193*", "819**", "81***", "8****", "*****");
        
        // Add hierarchies to data definition, this will change attribute type to quasi-idnetifier 
        data.getDefinition().setAttributeType("age", ageHierarchy);
        data.getDefinition().setAttributeType("gender", genderHierarchy);
        data.getDefinition().setAttributeType("zipcode", zipcodeHierarchy);


Related examples: [Example1]()

[Go to contents](#api-howtos)

### Import or read data and hierarchies from csv files

        import org.deidentifier.arx.AttributeType.Hierarchy;
        import org.deidentifier.arx.Data;

        // import data from csv file
        Data data = Data.create("data/test.csv", StandardCharsets.UTF_8, ';');
        
        // Define hierarchies from csv files
        // the attribute types will be QUASI_IDENTIFYING_ATTRIBUTE
        data.getDefinition().setAttributeType("age", Hierarchy.create("data/test_hierarchy_age.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("gender", Hierarchy.create("data/test_hierarchy_gender.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("zipcode", Hierarchy.create("data/test_hierarchy_zipcode.csv", StandardCharsets.UTF_8, ';'));


Related examples: [Example2]()

[Go to contents](#api-howtos)

### Change Attribute data type e.g. to date


Related examples: [Example3]()

[Go to contents](#api-howtos)

### Change Attribute data type format e.g. to dd.MM.yyyy

Related examples: [Example1]()

[Go to contents](#api-howtos)

### Change Attribute type e.g. to sensitive or quasi-identifier

Attribute types: 

* IDENTIFYING_ATTRIBUTE: these attributes are associated with a high risk of re-identification. They will be removed from the dataset. Typical examples are names or Social Security Numbers.
* QUASI_IDENTIFYING_ATTRIBUTE: can in combination be used for re-identification attacks. They will be transformed. Typical examples are gender, date of birth and ZIP codes.
* SENSITIVE_ATTRIBUTE: these attributes encode properties with which individuals are not willing to be linked with. As such, they might be of interest to an attacker and, if disclosed, could cause harm to data subjects. They will be kept unmodified but may be subject to further constraints, such as t-closeness or l-diversity. Typical examples are diagnoses.
* INSENSITIVE_ATTRIBUTE: these attributes are not associated with privacy risks. They will be kept unmodified.

To change or check the attribute type of "age" attribute use this code: 

        // The default type is null:
        // If hierarchy is added then the type will be QUASI_IDENTIFYING_ATTRIBUTE  
        data.getDefinition().setAttributeType("age", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);

        // check the type
        System.out.println("age attribute type: " + data.getDefinition().getAttributeType("age"));


Related examples: [Example3]()

[Go to contents](#api-howtos)

### Export or write data and hierarchies to csv files

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create anonymization configueration

Related examples: [Example]()

[Go to contents](#api-howtos)

### Change anonymization configueration metric

Related examples: [Example]()

[Go to contents](#api-howtos)Cool

### Start anonymization process

Related examples: [Example]()

[Go to contents](#api-howtos)

### Print input and output data

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create subset research data manually

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create subset research data using simple selector (query)

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create subset research data using comblix selector (query)

Related examples: [Example]()

[Go to contents](#api-howtos)

