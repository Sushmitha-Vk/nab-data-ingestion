import json
import sys
import datetime
import socket
import requests
import glob
import time


uuid = '521ee16c-c1ad-4548-87fb-675b67fbaf17'
alfresco_url = 'http://ts-srch-1930-alb-979080691.ap-southeast-2.elb.amazonaws.com'
authHeaders = {"Authorization": "Basic YWRtaW46YWRtaW4="}
manifests_path = './nab-bulk-data'

concurrent_jobs_limit = 60


def get_running_jobs():
    statsurl = alfresco_url+'/alfresco/s/bulkobj/stats'
    resp = requests.get(statsurl, headers=authHeaders)
    print("status",resp.status_code)
    if resp.status_code != 200:
        print("This means something went wrong.")
        exit(255)

    # If we authenticated properly continue
    running_jobs=0
    uuids = resp.json()
    for uuid in uuids:
        if uuid["status"] == "RUNNING":
                running_jobs += 1
    return running_jobs

hostname = socket.gethostname()
now = datetime.datetime.now()
group=hostname + "_" + str(now.year) + "." + str(now.month) + "." + str(now.day) + "_" + str(now.hour) + "." + str(now.minute) + "." + str(now.second)
homedir=hostname + "/" + str(now.year) + "/" + str(now.month) + "." + str(now.day) + "/" + str(now.hour) +"." + str(now.minute)  +"." + str(now.second)

manifests={}
succeeded_jobs = 0
secceeded_ids = []
failed_jobs = 0


file_paths = glob.glob(manifests_path+"/*.json")
for path in file_paths:
    running_jobs = get_running_jobs()

    while running_jobs > concurrent_jobs_limit:
        print('Running jobs ('+str(running_jobs)+') exceeds the limit('+str(concurrent_jobs_limit)+'). Waiting..')
        time.sleep(30)
        running_jobs = get_running_jobs()

    with open(path) as f:
        manifests = json.load(f)
        aout = json.dumps(manifests,indent=2)

        if (uuid):
            print('Submitting job : ' + path)
            url = alfresco_url+'/alfresco/s/bulkobj/mapobjects/' + uuid + '?autoCreate=y&bgRun=y'
            try:
                resp = requests.post(url, headers=authHeaders,  verify=False, json=manifests)
                status_code = resp.status_code
                if (status_code == 200):
                    succeeded_jobs = succeeded_jobs + 1
                    resp_json = resp.json()
                    secceeded_ids.append(resp_json['id'])
                    print("Success : {}".format(resp_json['id']))
                else:
                    failed_jobs = failed_jobs + 1
                    print("Failure")
            except:
                failed_jobs = failed_jobs + 1
                print("Exception")

print("Succeeded jobs : {}".format(succeeded_jobs))
print("Succeeded job ids : {}".format(secceeded_ids))
print("Failed jobs: {}".format(failed_jobs))
