from fastapi import FastAPI
from pydantic import BaseModel
from http import HTTPStatus
import uvicorn
from fastapi.responses import JSONResponse
from decoder import get_audio_text
import os
from ML.machineLearning import get_results

app = FastAPI()


class RequestModel(BaseModel):
    data: str  
    id : str


@app.get("/ml")
def get_ml():
    return JSONResponse(status_code=HTTPStatus.OK, content={"message": "OK"})


@app.post("/ml")
async def post_ml(request: RequestModel):
    with open(f"./InputFiles/{request.id}.txt", "w") as file:
        file.write(request.data)


    transcription = await get_audio_text(id=request.id)
    topic, comment, spam, important  = get_results(transcription)



    input_file_path = f"{os.getcwd()}/InputFiles/{request.id}.txt"
    output_file_path =  f"{os.getcwd()}/OutputFiles/{request.id}.ogg" 

    print(input_file_path + "\n" + output_file_path)


    try:
        os.remove(input_file_path)
        os.remove(output_file_path)
    except Exception:
        print("cannot remove files")



    return JSONResponse(status_code=HTTPStatus.OK, content={
        "details" : transcription,
        "topic" : topic,
        "comments" : comment,
        "spam" : spam,
        "important" : important
    })


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=5000, log_level="info")


