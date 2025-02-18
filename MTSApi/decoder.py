import whisper
import base64
import os
import asyncio
import aiofiles

model = whisper.load_model("small")

async def get_audio_text(id: str):
    ogg_path = await transcribe_Base64To_Ogg(id=id)
    
    transcription = await transcribe_audio(audio_path=ogg_path)
    return transcription


async def transcribe_audio(audio_path: str, language: str = "ru"):
    """
    Transcribe the audio file using Whisper asynchronously.
    """
    loop = asyncio.get_event_loop()
    result = await loop.run_in_executor(
        None,  # Use the default ThreadPoolExecutor
        lambda: model.transcribe(audio_path, language=language)  # Pass arguments correctly
    )
    return result["text"]


async def transcribe_Base64To_Ogg(id: str):
    """
    Decode a Base64-encoded audio file and save it as an .ogg file asynchronously.
    """
    # Ensure the OutputFiles directory exists
    os.makedirs("OutputFiles", exist_ok=True)

    # Read the Base64 content from the .txt file asynchronously
    async with aiofiles.open(f"./InputFiles/{id}.txt", 'r') as file:
        content = await file.read()

    # Decode the Base64 content to binary data
    binary_data = base64.b64decode(content)

    # Save the binary data as an .ogg file asynchronously
    output_path = f"./OutputFiles/{id}.ogg"
    async with aiofiles.open(output_path, "wb") as ogg_file:
        await ogg_file.write(binary_data)
    
    return output_path



