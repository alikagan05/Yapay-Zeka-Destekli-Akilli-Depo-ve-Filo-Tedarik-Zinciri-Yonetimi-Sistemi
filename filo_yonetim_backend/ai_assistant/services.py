from groq import Groq
from django.conf import settings
from .models import AIChatMessage

class AIAssistantService:
    def __init__(self):
        self.client = Groq(api_key=settings.GROQ_API_KEY)
        self.model = "llama-3.3-70b-versatile"   # Güncel ve güçlü model

    def get_response(self, user_message: str, user=None) -> str:
        system_prompt = """
        Sen profesyonel bir lojistik, depo ve tedarik zinciri yönetim asistanısın. 
        Kullanıcıya net, profesyonel ve Türkçe cevap ver.
        Stok optimizasyonu, araç verimliliği, sevkiyat planlaması ve maliyet azaltma konularında uzmanlaş.
        Mümkünse sayısal önerilerde bulun.
        """

        try:
            chat_completion = self.client.chat.completions.create(
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": user_message}
                ],
                model=self.model,
                temperature=0.7,
                max_tokens=800,
            )

            response = chat_completion.choices[0].message.content

            if user:
                AIChatMessage.objects.create(
                    user=user,
                    message=user_message,
                    response=response
                )

            return response

        except Exception as e:
            return f"Üzgünüm, AI servisi şu anda yanıt veremiyor. Lütfen daha sonra tekrar dene. ({str(e)})"