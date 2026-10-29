from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions
from .services import AIAssistantService

class AIChatView(APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        user_message = request.data.get('message')

        if not user_message:
            return Response({"error": "Mesaj boş olamaz"}, status=status.HTTP_400_BAD_REQUEST)

        service = AIAssistantService()
        response = service.get_response(user_message, user=None)

        return Response({
            "user_message": user_message,
            "ai_response": response
        }, status=status.HTTP_200_OK)