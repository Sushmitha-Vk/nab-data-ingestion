import json
import requests
import psycopg2
import time
import datetime

solr_hosts = 'ip-10-0-2-244.ap-southeast-2.compute.internal,ip-10-0-2-203.ap-southeast-2.compute.internal,ip-10-0-2-241.ap-southeast-2.compute.internal,ip-10-0-2-7.ap-southeast-2.compute.internal,ip-10-0-2-234.ap-southeast-2.compute.internal'
solr_scheme = 'http'
solr_port = '8983'

pg_host = 'ts-acs61-nab-db-0.chc1vvifzyjv.ap-southeast-2.rds.amazonaws.com'
pg_user = 'alfresco'
pg_password = 'admin2019'
pg_database = 'alfresco'

es_index = 'nab-ingestion-2'
es_endpoint = 'https://search-ts-acs61-nab-7taekx3jryrjdp3il7ayczadnm.ap-southeast-2.es.amazonaws.com'

frequency_secs = 10

con = psycopg2.connect(
    host = pg_host,
    database = pg_database,
    user = pg_user,
    password = pg_password
)
cur = con.cursor()


def get_acs_count():
    acs_count = -1
    try: 
        cur.execute("select count(*) from alf_node where audit_creator='admin'")
        rows = cur.fetchall()
        acs_count=(rows[0][0])
    except: 
        print("Error while fetching data from database\n")

    return acs_count


es_endpoint = es_endpoint + '/' + es_index
headers_json = {'Content-type': 'application/json'}

def create_es_index():
    index_definition = '{ "mappings": { "ingestion" : { "properties": { "Type": { "type": "keyword" }, "Host": { "type": "keyword" }, "Count": { "type": "long" }, "Rate": { "type": "float" }, "Timestamp": { "format": "dateOptionalTime", "type": "date" } } }}}'
    resp = requests.put(es_endpoint, index_definition, headers = headers_json)
    return resp.status_code == 200

def has_es_index():
    resp = requests.get(es_endpoint)
    return resp.status_code == 200

def index_doc(rtype, host, count, rate):
    doc_definition = '{"Type": "'+ rtype +'", "Host": "'+host+'", "Count": '+str(count)+', "Rate": '+str(rate)+', "Timestamp": "'+datetime.datetime.utcnow().isoformat()+'" }'
    resp = requests.post(es_endpoint+'/ingestion', doc_definition, headers = headers_json)
    return resp.status_code == 201

def get_solr_count(solr_endpoint):
    count = -1
    try:
        solr_endpoint=solr_endpoint+'/solr/alfresco/query?q=*:*'
    
        resp = requests.get(solr_endpoint)
        if resp.status_code != 200:
            print("Something went wrong,corresponding endpoint is "+solr_endpoint)
        else:
            json_response = resp.json()
            count = json_response["response"]["numFound"]
            
    except requests.exceptions.RequestException as e: 
        print("Error {}\n".format(e))

    return count


if not has_es_index():
    create_es_index()
    print('Created ES Index \n')

rates = {}
times = {}
rates[pg_host] = 0
times[pg_host] = datetime.datetime.now()
hosts=solr_hosts.split(',')
for host in hosts:
    rates[host] = 0
    times[host] = datetime.datetime.now()

solr_rate = 0
acs_rate = 0

while not 1 == 2:
    hosts=solr_hosts.split(',')
    totalNoDocs=0
    for host in hosts:
        endpoint = solr_scheme + '://' + host + ':' + solr_port
        solr_count = get_solr_count(endpoint)
        print('solr count('+host+'): '+str(solr_count))

        current_time = datetime.datetime.now()
        time_diff = current_time - times[host]
        times[host] = current_time

        print('time diff '+ str(time_diff.seconds))

        if solr_count > -1:
            if rates[host] > 0:
                solr_rate = (solr_count - rates[host]) / time_diff.seconds
            rates[host] = solr_count
            index_doc('solr', host, solr_count, solr_rate)
    
    acs_count = get_acs_count()
    print('acs count('+pg_host+'): '+str(acs_count))

    current_time = datetime.datetime.now()
    time_diff = current_time - times[pg_host]
    times[pg_host] = current_time

    print('time diff '+ str(time_diff.seconds))

    if acs_count > -1:
        if rates[pg_host] > 0:
            acs_rate = (acs_count - rates[pg_host]) / time_diff.seconds
        rates[pg_host] = acs_count
        index_doc('acs', pg_host, acs_count, acs_rate)

    time.sleep(frequency_secs)

cur.close()
con.close()
